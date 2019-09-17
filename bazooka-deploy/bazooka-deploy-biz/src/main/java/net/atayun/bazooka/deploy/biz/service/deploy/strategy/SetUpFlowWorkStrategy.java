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
package net.atayun.bazooka.deploy.biz.service.deploy.strategy;

import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.bean.StrategyNumBean;
import net.atayun.bazooka.base.enums.deploy.AppOperationEventLogTypeEnum;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;
import net.atayun.bazooka.deploy.biz.enums.flow.DeployFlowEnum;
import net.atayun.bazooka.deploy.biz.log.LogConcat;
import net.atayun.bazooka.deploy.biz.service.deploy.strategy.setup.AbstractSetUpFlowStrategy;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.dto.EnvResourceDto;
import com.youyu.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static net.atayun.bazooka.deploy.biz.constants.DeployResultCodeConstants.*;

/**
 * @author Ping
 * @date 2019-07-12
 */
@Slf4j
@Component
@StrategyNum(
        superClass = AbstractDeployFlowWorkStrategy.class,
        number = DeployFlowEnum.FLOW_SET_UP,
        describe = "发布预备步骤(资源检查)")
public class SetUpFlowWorkStrategy extends AbstractDeployFlowWorkStrategy {

    @Autowired
    private EnvApi envApi;

    /**
     * 发布每个流程的具体实现
     *
     * @param workDetailPojo Flow所需所有数据实体
     */
    @Override
    public boolean doWorkDetail(WorkDetailPojo workDetailPojo) {

        AppDeployConfigDto appDeployConfig = workDetailPojo.getAppDeployConfig();

        AbstractSetUpFlowStrategy setUpStrategy = StrategyNumBean.getBeanInstance(AbstractSetUpFlowStrategy.class,
                appDeployConfig.getDeployMode().name());

        //分支或镜像校验
        setUpStrategy.setUpCheck(workDetailPojo);

        //资源校验
        checkResource(workDetailPojo.getDeployEntity().getEventId(), appDeployConfig);

        //校验完成后的操作
        setUpStrategy.afterAll(workDetailPojo);

        return true;
    }

    @Override
    public void cancel(DeployFlowEntity deployFlowEntity) {

    }

    /**
     * 检查当前发布配置
     *
     * @param deployConfig 发布配置
     */
    private void checkResource(Long eventId, AppDeployConfigDto deployConfig) {
        final LogConcat logConcat = new LogConcat(">> 2. 检查环境资源");
        try {
            String configName = deployConfig.getConfigName();
            Integer instance = deployConfig.getInstance();
            if (instance == 0) {
                throw new BizException(APP_INSTANCE_ZERO, String.format("发布配置[%s]实例数为0", configName));
            }

            EnvResourceDto envResource = envApi.getEnvAvailableResource(deployConfig.getEnvId())
                    .ifNotSuccessThrowException()
                    .getData();

            //cpus
            double totalShares = deployConfig.getCpus() * instance;
            boolean sharesIsAdequacy = envResource.getCpus()
                    .subtract(new BigDecimal(totalShares))
                    .compareTo(BigDecimal.ZERO) >= 0;
            logConcat.concat(String.format("CPU shares: [所需: %s, 剩余: %s]", deployConfig.getCpus() + " * " + instance, envResource.getCpus().toString()));
            if (!sharesIsAdequacy) {
                String msg = String.format("发布配置[%s]当前发布操作CPU资源不足, 请确认当前环境下是否有足够的CPU shares", configName);
                throw new BizException(ILLEGAL_CPU_SHARES, msg);
            }

            //mem
            long totalMem = deployConfig.getMemory() * instance;
            boolean memIsAdequacy = envResource.getMemory()
                    .subtract(new BigDecimal(totalMem))
                    .compareTo(BigDecimal.ZERO) >= 0;
            logConcat.concat(String.format("内存大小(MiB): [所需: %s, 剩余: %s]", deployConfig.getMemory() + " * " + instance, envResource.getMemory().toPlainString()));
            if (!memIsAdequacy) {
                String msg = String.format("发布配置[%s]当前发布操作内存资源不足, 请确认当前环境下是否有足够的内存", configName);
                throw new BizException(ILLEGAL_MEM, msg);
            }

            //disk
            long totalDisk = deployConfig.getDisk() * instance;
            boolean diskIsAdequacy = envResource.getDisk()
                    .subtract(new BigDecimal(totalDisk))
                    .compareTo(BigDecimal.ZERO) >= 0;
            logConcat.concat(String.format("磁盘容量(GiB): [所需: %s, 剩余: %s]", deployConfig.getDisk() + " * " + instance, envResource.getDisk().toPlainString()));
            if (!diskIsAdequacy) {
                String msg = String.format("发布配置[%s]当前发布操作磁盘资源不足, 请确认当前环境下是否有足够的磁盘容量", configName);
                throw new BizException(ILLEGAL_DISK, msg);
            }
            logConcat.concat("环境资源符合要求");
        } catch (Throwable throwable) {
            logConcat.concat(throwable);
            throw throwable;
        } finally {
            getAppOperationEventLog().save(eventId, AppOperationEventLogTypeEnum.APP_DEPLOY_FLOW_SET_UP,
                    2, logConcat.get());
        }
    }
}
