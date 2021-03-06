package net.atayun.bazooka.deploy.biz.v2.service.app.step;

import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.bean.StrategyNumBean;
import net.atayun.bazooka.base.constant.FlowStepConstants;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.log.StepLogBuilder;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.platform.Platform;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.dto.EnvDto;
import org.springframework.stereotype.Component;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = Step.class, number = FlowStepConstants.HEALTH_CHECK)
public class Step4HealthCheck extends Step implements Callback {

    @Override
    public void callback(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        EnvDto env = getBean(EnvApi.class).get(appOpt.getEnvId()).ifNotSuccessThrowException().getData();

        Platform platform = StrategyNumBean.getBeanInstance(Platform.class, env.getClusterType());

        platform.healthCheck(appOpt, appOptFlowStep, new StepLogBuilder());
    }

    @Override
    public void doWork(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        getStepLogCollector().collect(appOptFlowStep, "执行健康检查");
    }
}
