package net.atayun.bazooka.deploy.biz.v2.service.app.step;

import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.deploy.biz.v2.constant.FlowStepConstants;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.deploymode.DeployMode;
import org.springframework.stereotype.Component;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = Step.class, number = FlowStepConstants.BUILD_DOCKER_IMAGE)
public class Step4BuildDockerImage extends Step4DeployMode implements Callback {

    @Override
    protected void custom(DeployMode deployMode, AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        deployMode.buildDockerImage(appOpt, appOptFlowStep);
    }

    @Override
    public void callback(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {

    }
}
