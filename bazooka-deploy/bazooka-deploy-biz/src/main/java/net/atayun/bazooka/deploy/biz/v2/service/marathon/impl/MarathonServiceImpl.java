package net.atayun.bazooka.deploy.biz.v2.service.marathon.impl;

import lombok.extern.slf4j.Slf4j;
import mesosphere.marathon.client.Marathon;
import mesosphere.marathon.client.MarathonClient;
import mesosphere.marathon.client.model.v2.Deployment;
import net.atayun.bazooka.base.bean.StrategyNumBean;
import net.atayun.bazooka.base.constant.CommonConstants;
import net.atayun.bazooka.base.constant.FlowStepConstants;
import net.atayun.bazooka.base.enums.AppOptEnum;
import net.atayun.bazooka.base.enums.status.FinishStatusEnum;
import net.atayun.bazooka.deploy.api.param.MarathonCallbackParam;
import net.atayun.bazooka.deploy.api.param.MarathonTaskFailureCallbackParam;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.enums.FlowStepStatusEnum;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppOptService;
import net.atayun.bazooka.deploy.biz.v2.service.app.FlowStepService;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.Step;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.Step4HealthCheck;
import net.atayun.bazooka.deploy.biz.v2.service.marathon.MarathonService;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.dto.ClusterConfigDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
@Slf4j
@Service
public class MarathonServiceImpl implements MarathonService {

    @Autowired
    private AppOptService appOptService;

    @Autowired
    private FlowStepService flowStepService;

    @Override
    public void marathonCallback(MarathonCallbackParam marathonCallbackParam) {
        AppOpt appOpt = appOptService.selectByAppDeployUuidAndVersionForPlatform(marathonCallbackParam.getMarathonDeploymentId(), marathonCallbackParam.getMarathonDeploymentVersion());

        if (appOpt == null || appOpt.getOpt() != AppOptEnum.MARATHON_BUILD_DEPLOY) {
            return;
        }

        AppOptFlowStep appOptFlowStep = flowStepService.selectByOptIdAndStep(appOpt.getId(), FlowStepConstants.HEALTH_CHECK);

        if (appOptFlowStep == null) {
            return;
        }

        FinishStatusEnum finishStatus = marathonCallbackParam.getFinishStatus();

        FlowStepStatusEnum stepStatusEnum = FlowStepStatusEnum.valueOf(finishStatus.name());

        Map<String, Object> map = new HashMap<>();
        map.put("appDeployUuid", marathonCallbackParam.getMarathonDeploymentId());
        map.put("appDeployVersion", marathonCallbackParam.getMarathonDeploymentVersion());
        map.put("finishStatus", marathonCallbackParam.getFinishStatus());
        appOptFlowStep.setOutput(map);
        appOptFlowStep.setStatus(stepStatusEnum);

        Step4HealthCheck step = (Step4HealthCheck) StrategyNumBean.getBeanInstance(Step.class, FlowStepConstants.HEALTH_CHECK);
        try {
            step.callback(appOpt, appOptFlowStep);
        } catch (Throwable throwable) {
            appOptFlowStep.failure();
        }

        step.updateFlowStepAndPublish(appOpt, appOptFlowStep);
    }

    @Override
    public void marathonTaskFailureCallback(MarathonTaskFailureCallbackParam marathonTaskFailureCallbackParam) {
        String marathonServiceId = marathonTaskFailureCallbackParam.getMarathonServiceId();
        AppOpt appOpt = appOptService.selectByServiceIdAndVersionForPlatform(marathonServiceId,
                marathonTaskFailureCallbackParam.getMarathonDeploymentVersion());
        if (appOpt == null || appOpt.getOpt() != AppOptEnum.MARATHON_BUILD_DEPLOY) {
            return;
        }
        if (appOpt.isProcess()) {
            //保存失败log
            log.warn("发布task失败: {}", marathonTaskFailureCallbackParam.getMessage());
        }
        ClusterConfigDto clusterConf = getBean(EnvApi.class).getEnvConfiguration(appOpt.getEnvId())
                .ifNotSuccessThrowException().getData();
        String dcosUrl = CommonConstants.PROTOCOL + clusterConf.getDcosEndpoint() + CommonConstants.MARATHON_PORT;
        Marathon instance = MarathonClient.getInstance(dcosUrl);
        List<Deployment> deployments = instance.getDeployments();
        deployments.stream()
                .filter(deployment -> deployment.getAffectedApps().stream()
                        .anyMatch(app -> Objects.equals(marathonTaskFailureCallbackParam.getMarathonServiceId(), app)))
                .forEach(deployment -> {
                    try {
                        instance.cancelDeploymentAndRollback(deployment.getId());
                    } catch (Throwable throwable) {
                        log.warn("cancel deployment error: [" + deployment.toString() + "]", throwable);
                    }
                });
    }
}
