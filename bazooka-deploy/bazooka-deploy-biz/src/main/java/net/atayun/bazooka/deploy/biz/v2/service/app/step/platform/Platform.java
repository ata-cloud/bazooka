package net.atayun.bazooka.deploy.biz.v2.service.app.step.platform;

import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.log.StepLogBuilder;

/**
 * @author Ping
 */
public interface Platform {

    /**
     * 启动服务
     *
     * @param appOpt         appOpt
     * @param appOptFlowStep appOptFlowStep
     */
    void startApp(AppOpt appOpt, AppOptFlowStep appOptFlowStep, StepLogBuilder logBuilder);

    void stopApp(AppOpt appOpt, AppOptFlowStep appOptFlowStep, StepLogBuilder logBuilder);

    void restartApp(AppOpt appOpt, AppOptFlowStep appOptFlowStep, StepLogBuilder logBuilder);

    void scaleApp(AppOpt appOpt, AppOptFlowStep appOptFlowStep, StepLogBuilder logBuilder);

    void rollback(AppOpt appOpt, AppOptFlowStep appOptFlowStep, StepLogBuilder logBuilder);

    void deploy(AppOpt appOpt, AppOptFlowStep appOptFlowStep, StepLogBuilder logBuilder);

    void healthCheck(AppOpt appOpt, AppOptFlowStep appOptFlowStep, StepLogBuilder logBuilder);
}
