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
import net.atayun.bazooka.base.bean.StrategyNumBean;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.enums.AppOptStatusEnum;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppOptService;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppStatusOpt;
import net.atayun.bazooka.deploy.biz.v2.service.app.FlowStepService;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.Step;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

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
        log.info("scan doing event...");
        AppOptService appOptService = getBean(AppOptService.class);
        List<AppOpt> appOpts = appOptService.selectByStatus(AppOptStatusEnum.PROCESS);
        if (CollectionUtils.isEmpty(appOpts)) {
            log.info("all event finished...");
            return;
        }
        log.info("clean doing event...");
        FlowStepService flowStepService = getBean(FlowStepService.class);
        appOpts.forEach(appOpt -> {
            List<AppOptFlowStep> appOptFlowSteps = flowStepService.selectByOptId(appOpt.getId());
            if (!CollectionUtils.isEmpty(appOptFlowSteps)) {
                appOptFlowSteps.stream()
                        .filter(AppOptFlowStep::isProcess)
                        .min(Comparator.comparingInt(AppOptFlowStep::getStepSeq))
                        .ifPresent(appOptFlowStep -> {
                            Step step = StrategyNumBean.getBeanInstance(Step.class, appOptFlowStep.getStep());
                            step.cancel(appOpt, appOptFlowStep);
                        });
            }
            appOpt.failure();
            appOptService.update(appOpt);
            AppStatusOpt.updateAppStatus(appOpt, Boolean.FALSE);
        });
    }
}
