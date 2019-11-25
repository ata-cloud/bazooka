package net.atayun.bazooka.deploy.biz.v2.service.app.step;

import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.deploy.biz.v2.constant.FlowStepConstants;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.platform.Platform;
import org.springframework.stereotype.Component;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = Step.class, number = FlowStepConstants.RESTART_APP)
public class Step4RestartApp extends Step4Platform implements SinglePhase {

    @Override
    protected void custom(Platform platform, AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        platform.restartApp(appOpt, appOptFlowStep);
    }
}
