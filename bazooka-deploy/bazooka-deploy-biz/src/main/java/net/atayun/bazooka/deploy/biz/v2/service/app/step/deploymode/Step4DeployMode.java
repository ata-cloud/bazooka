package net.atayun.bazooka.deploy.biz.v2.service.app.step.deploymode;

import net.atayun.bazooka.base.bean.StrategyNumBean;
import net.atayun.bazooka.base.enums.deploy.DeployModeEnum;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.Step;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.feign.AppApi;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.dto.EnvDto;

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
        EnvDto envDto = getBean(EnvApi.class).get(appOpt.getEnvId()).ifNotSuccessThrowException().getData();
        String number = deployModeEnum == DeployModeEnum.DOCKER_IMAGE ? DeployModeEnum.MODE_DOCKER_IMAGE :
                DeployModeEnum.MODE_BUILD + envDto.getClusterType();
        DeployMode deployMode = StrategyNumBean.getBeanInstance(DeployMode.class, number);

        custom(deployMode, appOpt, appOptFlowStep);
    }

    protected abstract void custom(DeployMode deployMode, AppOpt appOpt, AppOptFlowStep appOptFlowStep);
}
