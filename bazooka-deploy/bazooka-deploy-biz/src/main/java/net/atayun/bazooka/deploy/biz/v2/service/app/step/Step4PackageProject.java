package net.atayun.bazooka.deploy.biz.v2.service.app.step;

import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.deploy.biz.v2.constant.FlowStepConstants;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import org.springframework.stereotype.Component;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = Step.class, number = FlowStepConstants.PACKAGE_PROJECT)
public class Step4PackageProject extends Step implements Callback {

    @Override
    public void doWork(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {

    }

    @Override
    public void callback(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {

    }
}
