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
package net.atayun.bazooka.gateway.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Ping
 */
@Slf4j
@Component
public class CleanDoingEventOnStart implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
//        log.info("scan doing event...");
//        AppOperationEventService appOperationEventService = getBean(AppOperationEventService.class);
//        List<AppOperationEventEntity> entities = appOperationEventService.selectByStatus(AppOperationEventStatusEnum.PROCESS);
//        if (CollectionUtils.isEmpty(entities)) {
//            log.info("all event finished...");
//            return;
//        }
//        log.info("clean doing event...");
//        AppApi appApi = getBean(AppApi.class);
//        DeployService deployService = getBean(DeployService.class);
//        DeployFlowService deployFlowService = getBean(DeployFlowService.class);
//        entities.forEach(entity -> {
//
//            List<DeployFlowEntity> deployFlowEntities = deployFlowService.selectByDeployId(entity.getId());
//            if (!CollectionUtils.isEmpty(deployFlowEntities)) {
//                deployFlowEntities.stream()
//                        .filter(deployFlowEntity -> deployFlowEntity.getFlowStatus() == BasicStatusEnum.PROCESS)
//                        .min(Comparator.comparingInt(DeployFlowEntity::getFlowNumber))
//                        .ifPresent(deployFlowEntity -> {
//                            AbstractDeployFlowWorkStrategy deployFlowWorkStrategy = StrategyNumBean.getBeanInstance(AbstractDeployFlowWorkStrategy.class,
//                                    deployFlowEntity.getFlowType().name());
//                            deployFlowWorkStrategy.cancel(deployFlowEntity);
//                        });
//            }
//
//            deployService.updateStatusByEventId(entity.getId(), BasicStatusEnum.FAILURE);
//
//            AppOperationEventEntity patch = new AppOperationEventEntity();
//            patch.setId(entity.getId());
//            patch.setStatus(AppOperationEventStatusEnum.FAILURE);
//            appOperationEventService.updateStatus(patch);
//
//            appApi.updateAppDeployStatus(entity.getAppId(), entity.getEnvId(), false, entity.getEvent());
//        });
    }
}
