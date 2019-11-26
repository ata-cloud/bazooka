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

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.atayun.bazooka.deploy.biz.dal.entity.deploy.DeployEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;
import net.atayun.bazooka.deploy.biz.enums.flow.DispatchFlowSourceEnum;
import net.atayun.bazooka.deploy.biz.enums.status.BasicStatusEnum;
import net.atayun.bazooka.deploy.biz.log.AppOperationEventLog;
import net.atayun.bazooka.deploy.biz.service.deploy.DeployService;
import net.atayun.bazooka.deploy.biz.service.deploy.event.DispatchFlowEvent;
import net.atayun.bazooka.deploy.biz.service.deploy.event.DispatchFlowEventPojo;
import net.atayun.bazooka.deploy.biz.service.flow.DeployFlowService;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.dto.AppInfoWithCredential;
import net.atayun.bazooka.pms.api.feign.AppApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * @author Ping
 * @date 2019-07-12
 */
@Slf4j
public abstract class AbstractDeployFlowWorkStrategy {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DeployFlowService deployFlowService;

    @Autowired
    private AppApi appApi;

    @Autowired
    private DeployService deployService;

    @Getter
    @Autowired
    private AppOperationEventLog appOperationEventLog;

    /**
     * 发布每个流程的具体实现
     *
     * @param deployId   发布Id
     * @param flowNumber 发布步骤编号
     */
    public void doWork(Long deployId, Integer flowNumber) {
        BasicStatusEnum statusEnum = BasicStatusEnum.SUCCESS;
        boolean isFinish = false;
        try {
            setFlowStatusToProcess(deployId, flowNumber);
            DeployFlowEntity deployFlowEntity = flowEntity(deployId, flowNumber);
            WorkDetailPojo workDetailPojo = initWorkDetailPoJo(deployFlowEntity);
            isFinish = doWorkDetail(workDetailPojo);
        } catch (Throwable t) {
            statusEnum = BasicStatusEnum.FAILURE;
            isFinish = true;
            log.warn(String.format("流程异常[deployId: %s, flowNumber: %s, ]", deployId, flowNumber), t);
        } finally {
            if (isFinish) {
                DispatchFlowEventPojo eventPojo = new DispatchFlowEventPojo(deployId, flowNumber, statusEnum);
                applicationContext.publishEvent(new DispatchFlowEvent(DispatchFlowSourceEnum.FLOW, eventPojo));
            }
        }
    }

    /**
     * 发布每个流程的具体实现
     *
     * @param workDetailPojo Flow所需所有数据实体
     * @return 是否完成
     */
    public abstract boolean doWorkDetail(WorkDetailPojo workDetailPojo);

    /**
     * 取消动作在dowWork执行后
     *
     * @param deployFlowEntity deployFlowEntity
     */
    public void cancel(DeployFlowEntity deployFlowEntity) {
    }

    /**
     * 查询流程Entity
     *
     * @param deployId   发布Id
     * @param flowNumber 流程号
     * @return 流程Entity
     */
    private DeployFlowEntity flowEntity(Long deployId, Integer flowNumber) {
        return deployFlowService.selectByDeployIdAndFlowNumber(deployId, flowNumber);
    }

    /**
     * 将流程状态更新为进行中
     *
     * @param deployId   deployId
     * @param flowNumber flowNumber
     */
    private void setFlowStatusToProcess(Long deployId, Integer flowNumber) {
        deployFlowService.updateFlowStatusByDeployIdAndFlowNumber(deployId, flowNumber, BasicStatusEnum.PROCESS);
    }

    /**
     * 初始化发布流程所需数据
     *
     * @param deployFlowEntity deployFlowEntity
     * @return WorkDetailPojo
     */
    private WorkDetailPojo initWorkDetailPoJo(DeployFlowEntity deployFlowEntity) {
        DeployEntity deployEntity = deployService.selectEntityById(deployFlowEntity.getDeployId());

        AppDeployConfigDto appDeployConfig = appApi.getAppDeployConfigInfoById(deployEntity.getDeployConfigId())
                .ifNotSuccessThrowException()
                .getData();

        AppInfoWithCredential appInfo = appApi.getAppInfoWithCredentialById(deployEntity.getAppId())
                .ifNotSuccessThrowException()
                .getData();

        return new WorkDetailPojo(deployEntity, deployFlowEntity, appInfo, appDeployConfig);
    }

}

