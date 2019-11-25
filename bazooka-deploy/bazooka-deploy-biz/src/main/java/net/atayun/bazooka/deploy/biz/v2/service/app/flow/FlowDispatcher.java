package net.atayun.bazooka.deploy.biz.v2.service.app.flow;

import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.enums.FlowStepStatusEnum;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.StepWorker;
import net.atayun.bazooka.deploy.biz.v2.service.app.threadpool.FlowStepThreadPool;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author Ping
 */
@Component
public class FlowDispatcher {

    @Async
    @EventListener
    public void dispatcher(FlowDispatcherEvent event) {
        StepWorker stepWorker = event.getStepWorker();
        AppOptFlowStep appOptFlowStep = stepWorker.getAppOptFlowStep();
        FlowStepStatusEnum status = appOptFlowStep.getStatus();
        if (status == FlowStepStatusEnum.STAND_BY) {
            FlowStepThreadPool.execute(stepWorker::doWork);
        } else if (status == FlowStepStatusEnum.SUCCESS) {
            //next
        } else if (status == FlowStepStatusEnum.FAILURE) {
            //finish
        }
    }
}
