package net.atayun.bazooka.deploy.biz.v2.service.app.step;

import lombok.Getter;
import net.atayun.bazooka.base.bean.StrategyNumBean;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.enums.FlowStepStatusEnum;

/**
 * @author Ping
 */
public class StepWorker {

    @Getter
    private AppOptFlowStep appOptFlowStep;

    @Getter
    private AppOpt appOpt;

    public StepWorker(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        this.appOptFlowStep = appOptFlowStep;
        this.appOpt = appOpt;
    }

    public void doWork() {
        Step step = StrategyNumBean.getBeanInstance(Step.class, appOptFlowStep.getStep());
        FlowStepStatusEnum status = FlowStepStatusEnum.SUCCESS;
        try {
            step.doWork(appOpt, appOptFlowStep);
        } catch (Throwable throwable) {
            status = FlowStepStatusEnum.FAILURE;
        } finally {
            if (status == FlowStepStatusEnum.SUCCESS) {
                if (step instanceof SinglePhase) {
                    step.notification(appOpt, appOptFlowStep, status);
                }
            } else {
                step.notification(appOpt, appOptFlowStep, status);
            }
        }

    }
}
