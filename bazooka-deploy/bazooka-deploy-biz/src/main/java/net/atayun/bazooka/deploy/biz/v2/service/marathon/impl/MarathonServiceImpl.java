package net.atayun.bazooka.deploy.biz.v2.service.marathon.impl;

import net.atayun.bazooka.base.bean.StrategyNumBean;
import net.atayun.bazooka.base.enums.status.FinishStatusEnum;
import net.atayun.bazooka.deploy.biz.v2.constant.FlowStepConstants;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.enums.FlowStepStatusEnum;
import net.atayun.bazooka.deploy.biz.v2.param.MarathonCallbackParam;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppOptService;
import net.atayun.bazooka.deploy.biz.v2.service.app.FlowStepService;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.Step;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.Step4HealthCheck;
import net.atayun.bazooka.deploy.biz.v2.service.marathon.MarathonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ping
 */
@Service
public class MarathonServiceImpl implements MarathonService {

    @Autowired
    private AppOptService appOptService;

    @Autowired
    private FlowStepService flowStepService;

    @Override
    public void marathonCallback(MarathonCallbackParam marathonCallbackParam) {
        AppOpt appOpt = appOptService.selectByAppDeployUuidAndVersion(marathonCallbackParam.getMarathonDeploymentId(), marathonCallbackParam.getMarathonDeploymentVersion());

        AppOptFlowStep appOptFlowStep = flowStepService.selectByOptIdAndStep(appOpt.getId(), FlowStepConstants.HEALTH_CHECK);

        FinishStatusEnum finishStatus = marathonCallbackParam.getFinishStatus();

        FlowStepStatusEnum stepStatusEnum = FlowStepStatusEnum.valueOf(finishStatus.name());

        Step4HealthCheck step = (Step4HealthCheck) StrategyNumBean.getBeanInstance(Step.class, FlowStepConstants.HEALTH_CHECK);
        try {
            step.callback(appOpt, appOptFlowStep);
        } catch (Throwable throwable) {
            stepStatusEnum = FlowStepStatusEnum.FAILURE;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("appDeployUuid", marathonCallbackParam.getMarathonDeploymentId());
        map.put("appDeployVersion", marathonCallbackParam.getMarathonDeploymentVersion());
        map.put("finishStatus", marathonCallbackParam.getFinishStatus());
        appOptFlowStep.setOutput(map);
        step.notification(appOpt, appOptFlowStep, stepStatusEnum);
    }
}
