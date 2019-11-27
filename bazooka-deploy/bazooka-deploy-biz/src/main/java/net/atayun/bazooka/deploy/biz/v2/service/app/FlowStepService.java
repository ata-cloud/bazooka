package net.atayun.bazooka.deploy.biz.v2.service.app;

import net.atayun.bazooka.deploy.biz.v2.dto.app.DeployingFlowDto;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;

import java.util.List;

/**
 * @author Ping
 */
public interface FlowStepService {

    void saveFlowSteps(List<AppOptFlowStep> flowSteps);

    AppOptFlowStep selectById(Long stepId);

    AppOptFlowStep selectByOptIdAndStep(Long optId, String step);

    AppOptFlowStep nextStep(AppOptFlowStep appOptFlowStep);

    Object getFromBeforeOutput(AppOptFlowStep appOptFlowStep, String key);

    void update(AppOptFlowStep appOptFlowStep);

    List<AppOptFlowStep> selectByOptId(Long optId);

    List<DeployingFlowDto> getDeployingFlow(Long optId);

    void cancel(AppOptFlowStep appOptFlowStep);
}
