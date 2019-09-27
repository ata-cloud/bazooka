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

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.enums.deploy.AppOperationEnum;
import net.atayun.bazooka.base.enums.status.FinishStatusEnum;
import net.atayun.bazooka.deploy.api.param.AppOperationEventParam;
import net.atayun.bazooka.deploy.biz.dal.entity.deploy.DeployEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;
import net.atayun.bazooka.deploy.biz.enums.flow.DeployFlowEnum;
import net.atayun.bazooka.deploy.biz.enums.flow.DispatchFlowSourceEnum;
import net.atayun.bazooka.deploy.biz.enums.status.BasicStatusEnum;
import net.atayun.bazooka.deploy.biz.service.deploy.DeployService;
import net.atayun.bazooka.deploy.biz.service.deploy.event.DispatchFlowEvent;
import net.atayun.bazooka.deploy.biz.service.deploy.event.DispatchFlowEventPojo;
import net.atayun.bazooka.deploy.biz.service.flow.DeployFlowService;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.feign.AppApi;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author Ping
 * @date 2019-07-25
 * @see AppOperationEnum#DEPLOY
 */
@Slf4j
@Component
@StrategyNum(superClass = AbstractAppOperationEvent.class,
        number = "DEPLOY",
        describe = "构建发布操作"
)
public class AppDeployOperationEvent extends AbstractAppOperationEvent implements ApplicationContextAware {

    @Autowired
    private DeployService deployService;

    @Autowired
    private DeployFlowService deployFlowService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AppApi appApi;

    @Override
    public String getEventRemark(AppOperationEventParam appOperationEventParam) {
        AppDeployOperationEventPojo appDeployOperationEventPojo = JSONObject.parseObject(appOperationEventParam.getDetail(),
                AppDeployOperationEventPojo.class);
        Long deployConfigId = appDeployOperationEventPojo.getDeployConfigId();
        AppDeployConfigDto appDeployConfig = appApi.getAppDeployConfigInfoById(deployConfigId)
                .ifNotSuccessThrowException()
                .getData();
        return "发布配置: " + appDeployConfig.getConfigName();
    }

    /**
     * 事件处理
     *
     * @param appOperationEventParam 参数
     * @param eventId                事件Id
     */
    @Override
    public void doWork(AppOperationEventParam appOperationEventParam, Long eventId) {
        AppDeployOperationEventPojo appDeployOperationEventPojo = JSONObject.parseObject(appOperationEventParam.getDetail(),
                AppDeployOperationEventPojo.class);
        deployService.deployAction(eventId, appDeployOperationEventPojo);
    }

    /**
     * marathon callback
     *
     * @param eventId 事件id
     * @param status  成功或者失败
     */
    @Transactional(rollbackFor = Throwable.class)
    public void marathonCallbackForDeploy(Long eventId, FinishStatusEnum status) {
        DeployEntity deployEntity = deployService.selectByEventId(eventId);
        if (deployEntity == null) {
            log.warn("事件[{}]无法获取发布记录", eventId);
            return;
        }
        Long deployId = deployEntity.getId();
        DeployFlowEntity deployFlowEntity = deployFlowService.selectByDeployIdAndFlowType(deployId, DeployFlowEnum.HEALTH_CHECK);
        Integer flowNumber = deployFlowEntity.getFlowNumber();
        BasicStatusEnum flowStatus = status == FinishStatusEnum.SUCCESS ? BasicStatusEnum.SUCCESS : BasicStatusEnum.FAILURE;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                DispatchFlowEventPojo eventPojo = new DispatchFlowEventPojo(deployId, flowNumber, flowStatus);
                applicationContext.publishEvent(new DispatchFlowEvent(DispatchFlowSourceEnum.MARATHON_EVENT, eventPojo));
            }
        });
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
