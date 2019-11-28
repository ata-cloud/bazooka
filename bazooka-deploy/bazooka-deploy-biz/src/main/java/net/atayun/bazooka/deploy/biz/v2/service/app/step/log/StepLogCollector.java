package net.atayun.bazooka.deploy.biz.v2.service.app.step.log;

import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;

/**
 * @author Ping
 */
public interface StepLogCollector {

    void collect(AppOptFlowStep appOptFlowStep, String log);

    void merge(AppOptFlowStep appOptFlowStep);

    String get(AppOptFlowStep appOptFlowStep);
}
