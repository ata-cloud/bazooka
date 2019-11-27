package net.atayun.bazooka.deploy.biz.v2.service.marathon.impl;

import net.atayun.bazooka.base.bean.StrategyNumBean;
import net.atayun.bazooka.base.constant.FlowStepConstants;
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
        AppOpt appOpt = appOptService.selectByAppDeployUuidAndVersionForMarathon(marathonCallbackParam.getMarathonDeploymentId(), marathonCallbackParam.getMarathonDeploymentVersion());

        AppOptFlowStep appOptFlowStep = flowStepService.selectByOptIdAndStep(appOpt.getId(), FlowStepConstants.HEALTH_CHECK);

        FinishStatusEnum finishStatus = marathonCallbackParam.getFinishStatus();

        FlowStepStatusEnum stepStatusEnum = FlowStepStatusEnum.valueOf(finishStatus.name());

        Map<String, Object> map = new HashMap<>();
        map.put("appDeployUuid", marathonCallbackParam.getMarathonDeploymentId());
        map.put("appDeployVersion", marathonCallbackParam.getMarathonDeploymentVersion());
        map.put("finishStatus", marathonCallbackParam.getFinishStatus());
        appOptFlowStep.setOutput(map);

        Step4HealthCheck step = (Step4HealthCheck) StrategyNumBean.getBeanInstance(Step.class, FlowStepConstants.HEALTH_CHECK);
        try {
            step.callback(appOpt, appOptFlowStep);
        } catch (Throwable throwable) {
            stepStatusEnum = FlowStepStatusEnum.FAILURE;
        }

        step.notification(appOpt, appOptFlowStep, stepStatusEnum);
    }

    @Override
    public void marathonTaskFailureCallback(MarathonTaskFailureCallbackParam marathonTaskFailureCallbackParam) {
//        String marathonServiceId = marathonTaskFailureCallbackParam.getMarathonServiceId();
//        AppOperationEventMarathonEntity appOperationEventMarathonEntity = appOperationEventMarathonService.selectByServiceIdAndVersion(marathonServiceId,
//                marathonTaskFailureCallbackParam.getMarathonDeploymentVersion());
//        if (appOperationEventMarathonEntity == null) {
//            return;
//        }
//        Long eventId = appOperationEventMarathonEntity.getEventId();
//        AppOperationEventEntity appOperationEventEntity = getMapper().selectByPrimaryKey(eventId);
//        if (appOperationEventEntity.getStatus() == AppOperationEventStatusEnum.PROCESS) {
//            AppOperationEventLog eventLog = getBean(AppOperationEventLog.class);
//            AppOperationEnum event = appOperationEventEntity.getEvent();
//            String content = "当前操作失败:\n" + marathonTaskFailureCallbackParam.getMessage();
//            if (event == AppOperationEnum.DEPLOY) {
//                eventLog.save(eventId, AppOperationEventLogTypeEnum.APP_DEPLOY_FLOW_HEALTH_CHECK, 2, content);
//            } else {
//                eventLog.save(eventId, event.getLogType(), content);
//            }
//            log.warn("MarathonTask失败: serviceId: [{}], message: [{}]", marathonServiceId, content);
//        }
//        ClusterConfigDto clusterConf = getBean(EnvApi.class).getEnvConfiguration(appOperationEventEntity.getEnvId()).ifNotSuccessThrowException().getData();
//        String dcosUrl = CommonConstants.PROTOCOL + clusterConf.getDcosEndpoint() + CommonConstants.MARATHON_PORT;
//        Marathon instance = MarathonClient.getInstance(dcosUrl);
//        List<Deployment> deployments = instance.getDeployments();
//        deployments.stream()
//                .filter(deployment -> deployment.getAffectedApps().stream()
//                        .anyMatch(app -> Objects.equals(marathonTaskFailureCallbackParam.getMarathonServiceId(), app)))
//                .forEach(deployment -> {
//                    try {
//                        instance.cancelDeploymentAndRollback(deployment.getId());
//                    } catch (Throwable throwable) {
//                        log.warn("cancel deployment error: [" + deployment.toString() + "]", throwable);
//                    }
//                });
    }
}
