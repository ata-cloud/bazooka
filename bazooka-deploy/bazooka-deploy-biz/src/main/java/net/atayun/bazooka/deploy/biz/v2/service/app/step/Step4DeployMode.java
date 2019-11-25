package net.atayun.bazooka.deploy.biz.v2.service.app.step;

import net.atayun.bazooka.base.bean.StrategyNumBean;
import net.atayun.bazooka.base.enums.deploy.DeployModeEnum;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.deploymode.DeployMode;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.feign.AppApi;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
public abstract class Step4DeployMode extends Step {

    @Override
    public void doWork(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        AppApi appApi = getBean(AppApi.class);
        AppDeployConfigDto appDeployConfigDto = appApi.getAppDeployConfigInfoById(appOpt.getDeployConfigId())
                .ifNotSuccessThrowException().getData();
        DeployModeEnum deployModeEnum = appDeployConfigDto.getDeployMode();
        DeployMode deployMode = StrategyNumBean.getBeanInstance(DeployMode.class, deployModeEnum.name());

        custom(deployMode, appOpt, appOptFlowStep);
    }

    protected abstract void custom(DeployMode deployMode, AppOpt appOpt, AppOptFlowStep appOptFlowStep);
}
