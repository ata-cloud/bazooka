package net.atayun.bazooka.deploy.biz.v2.service.app.step.platform;

import net.atayun.bazooka.base.bean.StrategyNumBean;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppOptService;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.Step;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.log.StepLogBuilder;
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

        StepLogBuilder logBuilder = new StepLogBuilder();
        try {
            customWork(platform, appOpt, appOptFlowStep, logBuilder);
        } catch (Throwable t) {
            logBuilder.append(t);
            throw t;
        } finally {
            getStepLogCollector().collect(appOptFlowStep, logBuilder.build());
        }

        getBean(AppOptService.class).updateById(appOpt);
    }

    /**
     * customWork
     *
     * @param platform       platform
     * @param appOpt         appOpt
     * @param appOptFlowStep appOptFlowStep
     * @param stepLogBuilder stepLogBuilder
     */
    protected abstract void customWork(Platform platform, AppOpt appOpt, AppOptFlowStep appOptFlowStep, StepLogBuilder stepLogBuilder);
}
