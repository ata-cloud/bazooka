package net.atayun.bazooka.deploy.biz.v2.service.app.step;

import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.constant.CommonConstants;
import net.atayun.bazooka.deploy.biz.constants.JenkinsBuildJobConstants;
import net.atayun.bazooka.deploy.biz.constants.JenkinsPushDockerImageJobConstants;
import net.atayun.bazooka.deploy.biz.param.jenkins.PushImageCallbackParam;
import net.atayun.bazooka.deploy.biz.v2.constant.FlowStepConstants;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.jenkins.Step4Jenkins;
import net.atayun.bazooka.pms.api.PmsCredentialsApi;
import net.atayun.bazooka.pms.api.dto.AppInfoDto;
import net.atayun.bazooka.pms.api.dto.PmsCredentialsDto;
import net.atayun.bazooka.pms.api.feign.AppApi;
import net.atayun.bazooka.rms.api.RmsDockerImageApi;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.dto.ClusterConfigDto;
import net.atayun.bazooka.rms.api.dto.RmsDockerImagePushCheckResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = Step.class, number = FlowStepConstants.PUSH_DOCKER_IMAGE)
public class Step4PushDockerImage extends Step4Jenkins implements Callback {

    @Autowired
    private RmsDockerImageApi rmsDockerImageApi;

    @Autowired
    private EnvApi envApi;

    @Override
    public void doWork(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        Map<String, Object> detail = appOpt.getDetail();
        Boolean isExternalDockerRegistry = (Boolean) detail.getOrDefault("isExternalDockerRegistry", false);
        if (!isExternalDockerRegistry) {
            Long imageId = (Long) detail.get("imageId");
            Long targetEnvId = (Long) detail.get("targetEnvId");
            RmsDockerImagePushCheckResultDto data = rmsDockerImageApi.imagePushPreCheck(imageId, targetEnvId)
                    .ifNotSuccessThrowException().getData();
            boolean sameRegistry = data.isSameRegistry();
            if (sameRegistry) {
                rmsDockerImageApi.imageCopy(imageId, targetEnvId).ifNotSuccessThrowException();
                return;
            }
        }
        super.doWork(appOpt, appOptFlowStep);
    }

    @Override
    protected Map<String, String> getJobParam(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        ClusterConfigDto clusterConfig = envApi.getEnvConfiguration(appOpt.getEnvId())
                .ifNotSuccessThrowException().getData();
        AppInfoDto appInfo = getBean(AppApi.class).getAppInfoById(appOpt.getAppId())
                .ifNotSuccessThrowException().getData();
        Map<String, Object> detail = appOpt.getDetail();
        String targetDockerRegistry = getTargetDockerRegistry(detail);
        Map<String, String> param = new HashMap<>();
        param.put(JenkinsBuildJobConstants.OPT_ID, appOpt.getId().toString());
        param.put(JenkinsBuildJobConstants.STEP_ID, appOptFlowStep.getId().toString());
        param.put(JenkinsBuildJobConstants.BUILD_CALLBACK_URI, jenkinsJobPropertiesHelper.buildCallbackUri());
        param.put(JenkinsPushDockerImageJobConstants.SOURCE_DOCKER_REGISTRY, clusterConfig.getDockerHubUrl());
        param.put(JenkinsPushDockerImageJobConstants.TARGET_DOCKER_REGISTRY, targetDockerRegistry);
        Boolean needAuth = Optional.ofNullable(detail.get("needAuth")).map(o -> (Boolean) o).orElse(false);
        param.put(JenkinsPushDockerImageJobConstants.NEED_AUTH, needAuth.toString());
        if (needAuth) {
            Long credentialId = (Long) detail.get("credentialId");
            PmsCredentialsDto credentials = getBean(PmsCredentialsApi.class).getCredentialsDtoById(credentialId)
                    .ifNotSuccessThrowException().getData();
            param.put(JenkinsPushDockerImageJobConstants.USERNAME, credentials.getCredentialKey());
            param.put(JenkinsPushDockerImageJobConstants.PASSWORD, credentials.getCredentialValue());
        }
        param.put(JenkinsPushDockerImageJobConstants.DOCKER_IMAGE_NAME, appInfo.getDockerImageName());
        param.put(JenkinsPushDockerImageJobConstants.DOCKER_IMAGE_TAG, (String) detail.get("dockerImageTag"));
        param.put(JenkinsPushDockerImageJobConstants.IMAGE_ID, ((Long) detail.get("imageId")).toString());
        param.put(JenkinsPushDockerImageJobConstants.TARGET_ENV_ID, Optional.ofNullable(detail.get("targetEnvId")).map(Object::toString).orElse(""));
        return param;
    }

    private String getTargetDockerRegistry(Map<String, Object> detail) {
        String targetDockerRegistry;
        if ((Boolean) detail.getOrDefault("isExternalDockerRegistry", false)) {
            targetDockerRegistry = (String) detail.get("targetDockerRegistry");
        } else {
            targetDockerRegistry = envApi.getEnvConfiguration((Long) detail.get("targetEnvId"))
                    .ifNotSuccessThrowException()
                    .getData()
                    .getDockerHubUrl();
        }
        if (targetDockerRegistry.startsWith(CommonConstants.PROTOCOL)) {
            targetDockerRegistry = targetDockerRegistry.replace(CommonConstants.PROTOCOL, "");
        }
        if (targetDockerRegistry.startsWith(CommonConstants.SECURE_PROTOCOL)) {
            targetDockerRegistry = targetDockerRegistry.replace(CommonConstants.SECURE_PROTOCOL, "");
        }
        return targetDockerRegistry;
    }

    @Override
    protected String getJobName() {
        return this.jenkinsJobPropertiesHelper.randomPushDockerImageJobName();
    }

    @Override
    public void callback(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        Map<String, Object> custom = appOptFlowStep.getOutput();
        String targetEnvIdStr = (String) custom.get(PushImageCallbackParam.CUSTOM_KEY_TARGET_ENV_ID);
        if (StringUtils.hasText(targetEnvIdStr)) {
            long targetEnvId = Long.parseLong(targetEnvIdStr);
            long imageId = Long.parseLong((String) custom.get(PushImageCallbackParam.CUSTOM_KEY_IMAGE_ID));
            rmsDockerImageApi.imageCopy(imageId, targetEnvId).ifNotSuccessThrowException();
        }
    }
}
