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
import net.atayun.bazooka.base.bean.SpringContextBean;
import net.atayun.bazooka.base.enums.deploy.AppOperationEventLogTypeEnum;
import net.atayun.bazooka.base.enums.status.FinishStatusEnum;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;
import net.atayun.bazooka.deploy.biz.enums.flow.DeployFlowEnum;
import net.atayun.bazooka.deploy.biz.log.LogConcat;
import net.atayun.bazooka.deploy.biz.service.app.event.AppDeployOperationEvent;
import net.atayun.bazooka.deploy.biz.service.deploy.cancel.MarathonFlowCancel;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.dto.EnvDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Ping
 * @date 2019-07-12
 */
@Component
@StrategyNum(
        superClass = AbstractDeployFlowWorkStrategy.class,
        number = DeployFlowEnum.FLOW_HEALTH_CHECK,
        describe = "健康检查步骤")
public class HealthCheckFlowWorkStrategy extends AbstractDeployFlowWorkStrategy {

    @Autowired
    private EnvApi envApi;

    /**
     * 发布每个流程的具体实现
     *
     * @param workDetailPojo Flow所需所有数据实体
     * @see AppDeployOperationEvent#marathonCallbackForDeploy(Long, FinishStatusEnum)
     */
    @Override
    public boolean doWorkDetail(WorkDetailPojo workDetailPojo) {
        EnvDto envDto = envApi.get(workDetailPojo.getAppDeployConfig().getEnvId())
                .ifNotSuccessThrowException()
                .getData();
        final LogConcat logConcat = new LogConcat(">> 1. 健康检查");
        String dcosServiceId = "/" + envDto.getCode() + workDetailPojo.getAppInfo().getDcosServiceId();
        logConcat.concat("服务[" + dcosServiceId + "]正在发布...");
        getAppOperationEventLog().save(workDetailPojo.getDeployEntity().getEventId(),
                AppOperationEventLogTypeEnum.APP_DEPLOY_FLOW_HEALTH_CHECK, 1, logConcat.get());
        return false;
    }

    @Override
    public void cancel(DeployFlowEntity deployFlowEntity) {
        SpringContextBean.getBean(MarathonFlowCancel.class).cancel(deployFlowEntity);
    }
}
