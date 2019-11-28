package net.atayun.bazooka.deploy.biz.v2.service.app.step.deploymode;

import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.enums.deploy.DeployModeEnum;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.log.StepLogBuilder;
import org.springframework.stereotype.Component;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = DeployMode.class, number = DeployModeEnum.MODE_BUILD + "2")
public class DeployMode4NodeBuild implements DeployMode, ICheckBranch {

    @Override
    public void check(AppOpt appOpt, AppOptFlowStep appOptFlowStep, StepLogBuilder stepLogBuilder) {
        checkBranch(appOpt, stepLogBuilder);
    }

}
