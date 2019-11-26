package net.atayun.bazooka.deploy.biz.v2.service.app.step;

import com.alibaba.fastjson.JSONObject;
import com.youyu.common.enums.IsDeleted;
import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.deploy.biz.constants.JenkinsBuildJobConstants;
import net.atayun.bazooka.deploy.biz.constants.JenkinsPushDockerImageJobConstants;
import net.atayun.bazooka.deploy.biz.param.jenkins.GitCommit;
import net.atayun.bazooka.deploy.biz.v2.constant.FlowStepConstants;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.jenkins.Step4Jenkins;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.dto.AppInfoDto;
import net.atayun.bazooka.pms.api.dto.AppInfoWithCredential;
import net.atayun.bazooka.pms.api.feign.AppApi;
import net.atayun.bazooka.rms.api.RmsDockerImageApi;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.dto.ClusterConfigDto;
import net.atayun.bazooka.rms.api.dto.EnvDto;
import net.atayun.bazooka.rms.api.dto.RmsDockerImageDto;
import net.atayun.bazooka.rms.api.enums.DockerImageSource;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = Step.class, number = FlowStepConstants.BUILD_DOCKER_IMAGE)
public class Step4BuildDockerImage extends Step4Jenkins implements Callback {

    @Override
    public void callback(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        //保存镜像
        Map<String, Object> output = appOptFlowStep.getOutput();
        String dockerImageTag = (String) output.get("dockerImageTag");
        AppInfoDto appInfo = getBean(AppApi.class).getAppInfoById(appOpt.getAppId())
                .ifNotSuccessThrowException().getData();
        ClusterConfigDto clusterConfig = getBean(EnvApi.class).getEnvConfiguration(appOpt.getEnvId())
                .ifNotSuccessThrowException().getData();
        String branch = appOpt.getBranch();
        GitCommit gitCommit = JSONObject.parseObject((String) output.get("gitCommit"), GitCommit.class);
        RmsDockerImageDto rmsDockerImageDto = new RmsDockerImageDto();
        rmsDockerImageDto.setEnvId(appOpt.getEnvId());
        rmsDockerImageDto.setAppId(appOpt.getAppId());
        rmsDockerImageDto.setRegistry(clusterConfig.getDockerHubUrl());
        rmsDockerImageDto.setImageName(appInfo.getDockerImageName());
        rmsDockerImageDto.setImageTag(dockerImageTag);
        rmsDockerImageDto.setIsDeleted(IsDeleted.NOT_DELETED);
        rmsDockerImageDto.setGitUrl(appInfo.getGitlabUrl());
        rmsDockerImageDto.setGitBranch(branch);
        rmsDockerImageDto.setGitCommitId(gitCommit.getSha());
        rmsDockerImageDto.setGitCommitMgs(gitCommit.getMessage());
        LocalDateTime gitCommitTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(gitCommit.getUnixTimestamp()),
                ZoneId.systemDefault());
        rmsDockerImageDto.setGitCommitTime(gitCommitTime);
        rmsDockerImageDto.setSource(DockerImageSource.Build);
        rmsDockerImageDto.setImageCreateTime(LocalDateTime.now());
        getBean(RmsDockerImageApi.class).create(rmsDockerImageDto).ifNotSuccessThrowException();
    }

    @Override
    protected Map<String, String> getJobParam(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        AppDeployConfigDto appDeployConfig = getBean(AppApi.class).getAppDeployConfigInfoById(appOpt.getDeployConfigId())
                .ifNotSuccessThrowException().getData();
        AppInfoWithCredential appInfo = getBean(AppApi.class).getAppInfoWithCredentialById(appOpt.getAppId())
                .ifNotSuccessThrowException().getData();
        ClusterConfigDto clusterConfig = getBean(EnvApi.class).getEnvConfiguration(appDeployConfig.getEnvId())
                .ifNotSuccessThrowException()
                .getData();
        EnvDto env = getBean(EnvApi.class).get(appDeployConfig.getEnvId())
                .ifNotSuccessThrowException()
                .getData();
        Map<String, String> param = new HashMap<>();
        param.put(JenkinsBuildJobConstants.OPT_ID, appOpt.getId().toString());
        param.put(JenkinsBuildJobConstants.STEP_ID, appOptFlowStep.getId().toString());
        param.put(JenkinsBuildJobConstants.BUILD_CALLBACK_URI, jenkinsJobPropertiesHelper.buildCallbackUri());
        param.put(JenkinsBuildJobConstants.DOCKER_REGISTRY, clusterConfig.getDockerHubUrl());
        param.put(JenkinsBuildJobConstants.DOCKER_IMAGE_NAME, appInfo.getDockerImageName());
        String dockerImageTag = String.join("-", env.getCode(), appOpt.getBranch(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd-HHmm")));
        param.put(JenkinsBuildJobConstants.DOCKER_IMAGE_TAG, dockerImageTag);
        param.put(JenkinsBuildJobConstants.DOCKERFILE_PATH, appDeployConfig.getDockerfilePath());
        param.put(JenkinsPushDockerImageJobConstants.NEED_AUTH, "");
        param.put(JenkinsPushDockerImageJobConstants.USERNAME, "");
        param.put(JenkinsPushDockerImageJobConstants.PASSWORD, "");
        return param;
    }

    @Override
    protected String getJobName() {
        return this.jenkinsJobPropertiesHelper.randomBuildDockerImage();
    }
}
