package net.atayun.bazooka.deploy.biz.v2.service.app.flow;

import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.enums.AppOptStatusEnum;
import net.atayun.bazooka.deploy.biz.v2.enums.FlowStepStatusEnum;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppOptService;
import net.atayun.bazooka.deploy.biz.v2.service.app.FlowStepService;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.StepWorker;
import net.atayun.bazooka.deploy.biz.v2.service.app.threadpool.FlowStepThreadPool;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

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
        AppOpt appOpt = stepWorker.getAppOpt();

        boolean flowFinish = false;
        if (status == FlowStepStatusEnum.STAND_BY) {
            FlowStepThreadPool.execute(stepWorker::doWork);
        } else if (status == FlowStepStatusEnum.SUCCESS) {
            //next
            AppOptFlowStep nextStep = getBean(FlowStepService.class).nextStep(appOptFlowStep);
            if (nextStep == null) {
                //finish
                flowFinish = true;
                appOpt.setStatus(AppOptStatusEnum.SUCCESS);
            } else {
                nextStep.setInput(appOptFlowStep.getOutput());
                FlowStepThreadPool.execute(() -> new StepWorker(appOpt, nextStep).doWork());
            }
        } else if (status == FlowStepStatusEnum.FAILURE) {
            //failure -> finish
            flowFinish = true;
            appOpt.setStatus(AppOptStatusEnum.FAILURE);
        }
        if (flowFinish) {
            getBean(AppOptService.class).updateStatus(appOpt.getId(), appOpt.getStatus());
        }
    }
}
