package net.atayun.bazooka.deploy.biz.v2.service.app.impl;

import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.FlowStepService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Ping
 */
@Service
public class FlowStepServiceImpl implements FlowStepService {

    @Override
    public void saveFlowSteps(List<AppOptFlowStep> flowSteps) {

    }

    @Override
    public AppOptFlowStep selectById(Long stepId) {
        return null;
    }

    @Override
    public AppOptFlowStep selectByOptIdAndStep(Long optId, String healthCheck) {
        return null;
    }
}
