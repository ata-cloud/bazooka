package net.atayun.bazooka.deploy.biz.v2.service.app.step;

import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.constant.FlowStepConstants;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.deploymode.DeployMode;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.deploymode.Step4DeployMode;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.log.StepLogBuilder;
import org.springframework.stereotype.Component;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = Step.class, number = FlowStepConstants.SET_UP)
public class Step4SetUp extends Step4DeployMode implements SinglePhase {

    @Override
    protected void custom(DeployMode deployMode, AppOpt appOpt, AppOptFlowStep appOptFlowStep, StepLogBuilder stepLogBuilder) {
        deployMode.check(appOpt, appOptFlowStep, stepLogBuilder);
    }
}
