/*
 *    Copyright 2018-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.atayun.bazooka.deploy.biz.service.app.event;

import com.youyu.common.exception.BizException;
import mesosphere.client.common.ModelUtils;
import mesosphere.marathon.client.Marathon;
import mesosphere.marathon.client.MarathonClient;
import mesosphere.marathon.client.model.v2.App;
import mesosphere.marathon.client.model.v2.Result;
import net.atayun.bazooka.base.constant.CommonConstants;
import net.atayun.bazooka.base.enums.deploy.AppOperationEnum;
import net.atayun.bazooka.deploy.api.param.AppOperationEventParam;
import net.atayun.bazooka.deploy.biz.constants.MarathonAppConfigConstants;
import net.atayun.bazooka.deploy.biz.dal.entity.app.AppOperationEventMarathonEntity;
import net.atayun.bazooka.deploy.biz.log.AppOperationEventLog;
import net.atayun.bazooka.deploy.biz.log.LogConcat;
import net.atayun.bazooka.deploy.biz.service.app.AppOperationEventMarathonService;
import net.atayun.bazooka.pms.api.dto.AppInfoDto;
import net.atayun.bazooka.pms.api.feign.AppApi;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.dto.ClusterConfigDto;
import net.atayun.bazooka.rms.api.dto.EnvDto;
import net.atayun.bazooka.rms.api.dto.EnvResourceDto;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Optional;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;
import static net.atayun.bazooka.deploy.biz.constants.DeployResultCodeConstants.*;

/**
 * @author Ping
 * @date 2019-07-26
 */
public abstract class AbstractAppOperationEventWithCallMarathon extends AbstractAppOperationEvent {

    @Autowired
    private AppOperationEventMarathonService appOperationEventMarathonService;

    @Autowired
    private EnvApi envApi;

    @Autowired
    private AppApi appApi;

    /**
     * 事件处理
     *
     * @param appOperationEventParam 参数
     * @param eventId                事件Id
     */
    @Override
    public void doWork(AppOperationEventParam appOperationEventParam, Long eventId) {
        @NotNull String detail = appOperationEventParam.getDetail();
        @NotNull Long appId = appOperationEventParam.getAppId();
        @NotNull Long envId = appOperationEventParam.getEnvId();
        String marathonConfig = getMarathonConfig(detail);
        EnvResourceDto envResource = envApi.getEnvAvailableResource(envId)
                .ifNotSuccessThrowException()
                .getData();
        App app = ModelUtils.GSON.fromJson(marathonConfig, App.class);
        app.getLabels().put(MarathonAppConfigConstants.LABEL_ATA_EVENT_ID, eventId.toString());
        customAppSetting(detail, app);
        checkResource(eventId, appOperationEventParam.getEvent(), envResource, app);
        ClusterConfigDto clusterConfig = envApi.getEnvConfiguration(envId)
                .ifNotSuccessThrowException()
                .getData();
        AppInfoDto appInfo = appApi.getAppInfoById(appId)
                .ifNotSuccessThrowException()
                .getData();
        EnvDto envDto = envApi.get(envId)
                .ifNotSuccessThrowException()
                .getData();
        Marathon marathon = MarathonClient.getInstance(CommonConstants.PROTOCOL + clusterConfig.getDcosEndpoint() + CommonConstants.MARATHON_PORT);
        String serviceId = "/" + envDto.getCode() + appInfo.getDcosServiceId();
        Result result = marathon.updateApp(serviceId, app, true);
        AppOperationEventMarathonEntity newMarathonEntity = new AppOperationEventMarathonEntity();
        newMarathonEntity.setMarathonConfig(app.toString());
        newMarathonEntity.setEventId(eventId);
        newMarathonEntity.setMarathonServiceId(serviceId);
        newMarathonEntity.setMarathonDeploymentId(result.getDeploymentId());
        newMarathonEntity.setMarathonDeploymentVersion(result.getVersion());
        appOperationEventMarathonService.insertEntity(newMarathonEntity);
    }

    private void checkResource(Long eventId, @NotNull AppOperationEnum event, EnvResourceDto envResource, App app) {
        AppOperationEventLog bean = getBean(AppOperationEventLog.class);
        BigDecimal instance = new BigDecimal(app.getInstances());
        LogConcat logConcat = new LogConcat();
        try {
            logConcat.concat(String.format("实例个数: %d", instance.intValue()));

            logConcat.concat(String.format("CPU shares: [所需: %s, 剩余: %s]", app.getCpus() + " * " + instance, envResource.getCpus().toString()));
            boolean sharesIsAdequacy = envResource.getCpus()
                    .subtract(new BigDecimal(app.getCpus()).multiply(instance))
                    .compareTo(BigDecimal.ZERO) >= 0;
            if (!sharesIsAdequacy) {
                throw new BizException(ILLEGAL_CPU_SHARES, "CPU share不足");
            }

            logConcat.concat(String.format("内存大小(MiB): [所需: %s, 剩余: %s]", app.getMem() + " * " + instance, envResource.getMemory().toString()));
            boolean memIsAdequacy = envResource.getMemory()
                    .subtract(new BigDecimal(app.getMem()).multiply(instance))
                    .compareTo(BigDecimal.ZERO) >= 0;
            if (!memIsAdequacy) {
                throw new BizException(ILLEGAL_MEM, "内存不足");
            }

            Double disk = Optional.ofNullable(app.getDisk()).orElse(0D);
            logConcat.concat(String.format("磁盘容量: [所需: %s, 剩余: %s]", disk + " * " + instance, envResource.getDisk().toString()));
            boolean diskIsAdequacy = envResource.getDisk()
                    .subtract(new BigDecimal(disk).multiply(instance))
                    .compareTo(BigDecimal.ZERO) >= 0;
            if (!diskIsAdequacy) {
                throw new BizException(ILLEGAL_DISK, "磁盘不足");
            }
        } catch (Throwable throwable) {
            logConcat.concat(throwable);
            throw throwable;
        } finally {
            bean.save(eventId, event.getLogType(), logConcat.get());
        }

        bean.save(eventId, event.getLogType(), "\n\n发布配置:");
        bean.save(eventId, event.getLogType(), app.toString());
    }

    /**
     * 获取Marathon配置
     *
     * @param detail detail
     * @return marathon config
     */
    public abstract String getMarathonConfig(String detail);

    /**
     * 自定义配置服务Marathon配置
     *
     * @param detail 详情
     * @param app    Marathon app
     */
    protected abstract void customAppSetting(String detail, App app);
}
