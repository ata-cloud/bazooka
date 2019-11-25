package net.atayun.bazooka.deploy.biz.v2.service.app;

import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;

import java.util.List;

/**
 * @author Ping
 */
public interface FlowStepService {
    void saveFlowStep(List<AppOptFlowStep> flowSteps);
}
