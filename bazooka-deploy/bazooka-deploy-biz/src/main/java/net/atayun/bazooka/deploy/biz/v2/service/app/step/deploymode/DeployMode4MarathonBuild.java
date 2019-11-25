package net.atayun.bazooka.deploy.biz.v2.service.app.step.deploymode;

import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.enums.deploy.DeployModeEnum;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import org.springframework.stereotype.Component;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = DeployMode.class, number = DeployModeEnum.MODE_MARATHON_BUILD)
public class DeployMode4MarathonBuild implements DeployMode {

    @Override
    public void check(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {

    }

    @Override
    public void buildDockerImage(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {

    }

    public void checkBranch() {

    }

    public void checkResource() {

    }
}
