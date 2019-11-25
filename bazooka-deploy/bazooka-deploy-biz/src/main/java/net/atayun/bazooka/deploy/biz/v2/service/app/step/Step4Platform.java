package net.atayun.bazooka.deploy.biz.v2.service.app.step;

import net.atayun.bazooka.base.bean.StrategyNumBean;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppOptService;
import net.atayun.bazooka.deploy.biz.v2.service.app.platform.Platform;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.dto.EnvDto;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
public abstract class Step4Platform extends Step {

    @Override
    public void doWork(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        EnvDto env = getBean(EnvApi.class).get(appOpt.getEnvId()).ifNotSuccessThrowException().getData();

        Platform platform = StrategyNumBean.getBeanInstance(Platform.class, env.getClusterType());

        custom(platform, appOpt, appOptFlowStep);

        getBean(AppOptService.class).updateById(appOpt);
    }

    /**
     * custom
     *
     * @param platform       platform
     * @param appOpt         appOpt
     * @param appOptFlowStep appOptFlowStep
     */
    protected abstract void custom(Platform platform, AppOpt appOpt, AppOptFlowStep appOptFlowStep);
}
