package net.atayun.bazooka.deploy.biz.v2.service.app.step.startapp;

import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.StepTool4Platform;

/**
 * @author Ping
 */
public class StartAppStepTool extends StepTool4Platform {

    @Override
    public void doWork(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        this.platform.startApp(appOpt, appOptFlowStep);
    }
}
