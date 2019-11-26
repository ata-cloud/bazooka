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
package net.atayun.bazooka.deploy.biz.service.deploy.event;

import lombok.extern.slf4j.Slf4j;
import net.atayun.bazooka.base.bean.StrategyNumBean;
import net.atayun.bazooka.deploy.biz.dal.entity.deploy.DeployEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;
import net.atayun.bazooka.deploy.biz.enums.flow.DeployFlowEnum;
import net.atayun.bazooka.deploy.biz.enums.flow.DispatchFlowSourceEnum;
import net.atayun.bazooka.deploy.biz.enums.status.AppOperationEventStatusEnum;
import net.atayun.bazooka.deploy.biz.enums.status.BasicStatusEnum;
import net.atayun.bazooka.deploy.biz.log.AppOperationEventLog;
import net.atayun.bazooka.deploy.biz.service.deploy.DeployService;
import net.atayun.bazooka.deploy.biz.service.deploy.strategy.AbstractDeployFlowWorkStrategy;
import net.atayun.bazooka.deploy.biz.service.flow.DeployFlowService;
import net.atayun.bazooka.deploy.biz.service.status.AppStatusOpt;
import net.atayun.bazooka.deploy.biz.service.status.EventStatusOpt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 * @date 2019-07-11
 */
@Slf4j
@Component
public class DispatchFlowListener implements SmartApplicationListener, AppStatusOpt, EventStatusOpt {

    @Autowired
    private DeployService deployService;

    @Autowired
    private DeployFlowService deployFlowService;

    /**
     * Determine whether this listener actually supports the given event type.
     *
     * @param eventType 事件类型
     */
    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return DispatchFlowEvent.class.isAssignableFrom(eventType);
    }

    /**
     * Determine whether this listener actually supports the given source type.
     *
     * @param sourceType 事件源类型
     */
    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return DispatchFlowSourceEnum.class.isAssignableFrom(sourceType);
    }

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Async
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        DispatchFlowSourceEnum source = (DispatchFlowSourceEnum) event.getSource();
        log.debug("接受DispatchFlow事件,事件源[{}]", source.name());

        DispatchFlowEventPojo dispatchFlowEventPojo = ((DispatchFlowEvent) event).getDispatchFlowEventPojo();
        Long deployId = dispatchFlowEventPojo.getDeployId();
        Integer currentFlowNumber = dispatchFlowEventPojo.getCurrentFlowNumber();
        if (source != DispatchFlowSourceEnum.DEPLOY_ACTION) {
            DeployFlowEntity currentDeployFlow = deployFlowService.selectByDeployIdAndFlowNumber(deployId, currentFlowNumber);
            DeployFlowEnum flowType = currentDeployFlow.getFlowType();

            BasicStatusEnum currentFlowStatus = dispatchFlowEventPojo.getCurrentFlowStatus();
            deployFlowService.updateFlowStatusByDeployIdAndFlowNumber(deployId, currentFlowNumber, currentFlowStatus);

            DeployEntity deployEntity = deployService.selectEntityById(deployId);
            Long eventId = deployEntity.getEventId();
            getBean(AppOperationEventLog.class).mergePartFile(eventId, flowType.getLogType());
            getBean(AppOperationEventLog.class).save(eventId, flowType.getLogType(), "\n[" + flowType.getDescription() + ": " + currentFlowStatus.getDescription() + "]");

            if (currentFlowStatus == BasicStatusEnum.FAILURE) {
                log.warn("发布流程异常[deployId: {}, deployFlowNumber: {}, deployFlowStatus: {}]", deployId, currentFlowNumber, currentFlowStatus);
                deployService.updateStatus(deployId, BasicStatusEnum.FAILURE);
                updateEventStatus(eventId, AppOperationEventStatusEnum.FAILURE);
                updateAppStatus(eventId, false);
                return;
            }

            //如果将deploy状态已经通过其它方式置为失败(例如: ata服务重启), 则无需继续进行后续步骤
            if (deployEntity.getStatus() == BasicStatusEnum.FAILURE) {
                return;
            }
        }

        Integer nextFlowNumber = currentFlowNumber + 1;
        DeployFlowEntity deployFlowEntity = deployFlowService.selectByDeployIdAndFlowNumber(deployId, nextFlowNumber);

        //流程是否全部执行完成
        if (deployFlowEntity == null) {
            deployService.updateStatus(deployId, BasicStatusEnum.SUCCESS);
            log.debug("发布流程结束[{}]", deployId);
            return;
        }

        AbstractDeployFlowWorkStrategy deployFlowWorkStrategy = StrategyNumBean.getBeanInstance(AbstractDeployFlowWorkStrategy.class,
                deployFlowEntity.getFlowType().name());
        deployFlowWorkStrategy.doWork(deployId, nextFlowNumber);
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
