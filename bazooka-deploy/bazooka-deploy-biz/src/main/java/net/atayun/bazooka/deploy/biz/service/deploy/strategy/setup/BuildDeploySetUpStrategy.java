/*
 *    Copyright 2018-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.atayun.bazooka.deploy.biz.service.deploy.strategy.setup;

import com.youyu.common.exception.BizException;
import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.enums.deploy.AppOperationEventLogTypeEnum;
import net.atayun.bazooka.base.enums.deploy.DeployModeEnum;
import net.atayun.bazooka.base.git.GitServiceHelp;
import net.atayun.bazooka.base.jenkins.JenkinsJobPropertiesHelper;
import net.atayun.bazooka.base.jenkins.JenkinsServerHelper;
import net.atayun.bazooka.deploy.biz.constants.DeployResultCodeConstants;
import net.atayun.bazooka.deploy.biz.constants.JenkinsBuildJobConstants;
import net.atayun.bazooka.deploy.biz.dal.entity.deploy.DeployEntity;
import net.atayun.bazooka.deploy.biz.log.LogConcat;
import net.atayun.bazooka.deploy.biz.service.deploy.strategy.WorkDetailPojo;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.dto.AppInfoWithCredential;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.dto.ClusterConfigDto;
import net.atayun.bazooka.rms.api.dto.EnvDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ping
 * @date 2019-07-22
 */
@Component
@StrategyNum(superClass = AbstractSetUpFlowStrategy.class,
        number = DeployModeEnum.MODE_BUILD,
        describe = "构建发布的预备工作"
)
public class BuildDeploySetUpStrategy extends AbstractSetUpFlowStrategy {

    @Autowired
    private JenkinsServerHelper jenkinsServerHelper;

    @Autowired
    private JenkinsJobPropertiesHelper jenkinsJobPropertiesHelper;

    @Autowired
    private EnvApi envApi;

    @Autowired
    private GitServiceHelp gitServiceHelp;

    /**
     * 发布流中 sep_up中的部分资源检查
     * {@link DeployModeEnum#BUILD} 需要校验分支是否存在且有效
     *
     * @param workDetailPojo 实体数据
     */
    @Override
    public void
    setUpCheck(WorkDetailPojo workDetailPojo) {
//        String branch = workDetailPojo.getDeployEntity().getBranch();
//        final LogConcat logConcat = new LogConcat(">> 1. 检查分支(" + branch + ")");
//        try {
//            String gitBranchAllow = workDetailPojo.getAppDeployConfig().getGitBranchAllow();
//            String gitBranchDeny = workDetailPojo.getAppDeployConfig().getGitBranchDeny();
//            if (StringUtils.isEmpty(gitBranchAllow) && StringUtils.isEmpty(gitBranchDeny)) {
//                logConcat.concat("允许所有分支");
//            } else {
//                if (StringUtils.hasText(gitBranchAllow)) {
//                    gitBranchAllow = gitBranchAllow.replace("*", "");
//                    if (!branch.contains(gitBranchAllow)) {
//                        throw new BizException(DeployResultCodeConstants.ILLEGAL_BRANCH, String.format("分支无效[只允许以%s为前缀的分支]", gitBranchAllow));
//                    }
//                } else {
//                    if (StringUtils.hasText(gitBranchDeny)) {
//                        gitBranchDeny = gitBranchDeny.replace("*", "");
//                        if (branch.contains(gitBranchDeny)) {
//                            throw new BizException(DeployResultCodeConstants.ILLEGAL_BRANCH, String.format("分支无效[不允许以%s为前缀的分支]", gitBranchDeny));
//                        }
//                    } else {
//                        throw new BizException(DeployResultCodeConstants.ILLEGAL_BRANCH, "未设置分支约束");
//                    }
//                }
//            }
//            AppInfoWithCredential appInfo = workDetailPojo.getAppInfo();
//            Set<String> branchSet = gitServiceHelp.branchList(appInfo.getGitlabUrl(), appInfo.getCredentialKey(), appInfo.getCredentialValue());
//            branchSet.stream()
//                    .filter(bch -> Objects.equals(bch, branch))
//                    .findAny()
//                    .orElseThrow(() -> new BizException(DeployResultCodeConstants.ILLEGAL_BRANCH, String.format("分支[%s]不存在", branch)));
//            logConcat.concat("分支符合要求");
//        } catch (Throwable throwable) {
//            logConcat.concat(throwable);
//            throw throwable;
//        } finally {
//            getAppOperationEventLog().save(workDetailPojo.getDeployEntity().getEventId(),
//                    AppOperationEventLogTypeEnum.APP_DEPLOY_FLOW_SET_UP, 1, logConcat.get());
//        }
    }

    /**
     * 准备工作完成后的操作
     *
     * @param workDetailPojo 实体数据
     */
    @Override
    public void afterAll(WorkDetailPojo workDetailPojo) {
//        AppInfoWithCredential appInfo = workDetailPojo.getAppInfo();
//        DeployEntity deployEntity = workDetailPojo.getDeployEntity();
//        AppDeployConfigDto appDeployConfig = workDetailPojo.getAppDeployConfig();
//
//        final LogConcat logConcat = new LogConcat(">> 3. 调用JenkinsJob");
//        try {
//            ClusterConfigDto clusterConfig = envApi.getEnvConfiguration(appDeployConfig.getEnvId())
//                    .ifNotSuccessThrowException()
//                    .getData();
//            EnvDto env = envApi.get(appDeployConfig.getEnvId())
//                    .ifNotSuccessThrowException()
//                    .getData();
//            String branch = deployEntity.getBranch();
//
//            Map<String, String> param = new HashMap<>(JenkinsBuildJobConstants.BUILD_JOB_PARAM_COUNTS);
//            param.put(JenkinsBuildJobConstants.GIT_REPOSITORY_URL, addUserInfo(appInfo.getGitlabUrl(), appInfo));
//            param.put(JenkinsBuildJobConstants.SERVICE_NAME, appInfo.getAppCode());
//            param.put(JenkinsBuildJobConstants.BRANCH, branch);
//            param.put(JenkinsBuildJobConstants.BUILD_SCRIPT_CALLBACK_URI, jenkinsJobPropertiesHelper.buildScriptCallbackUri());
//            param.put(JenkinsBuildJobConstants.EVENT_ID, deployEntity.getEventId().toString());
//            param.put(JenkinsBuildJobConstants.DEPLOY_ID, deployEntity.getId().toString());
//            param.put(JenkinsBuildJobConstants.COMPILE_COMMAND, appDeployConfig.getCompileCommand());
//            param.put(JenkinsBuildJobConstants.DOCKER_REGISTRY, clusterConfig.getDockerHubUrl());
//            param.put(JenkinsBuildJobConstants.DOCKER_IMAGE_NAME, appInfo.getDockerImageName());
//            String dockerImageTag = String.join("-", env.getCode(), branch,
//                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd-HHmm")));
//            param.put(JenkinsBuildJobConstants.DOCKER_IMAGE_TAG, dockerImageTag);
//            param.put(JenkinsBuildJobConstants.DOCKERFILE_PATH, appDeployConfig.getDockerfilePath());
//            param.put(JenkinsBuildJobConstants.BUILD_CALLBACK_URI, jenkinsJobPropertiesHelper.buildCallbackUri());
//            String buildJobName = jenkinsJobPropertiesHelper.randomBuildJobName();
//            jenkinsServerHelper.buildJob(buildJobName, param);
//            logConcat.concat("buildJobName: " + buildJobName);
//            logConcat.concat("jobParam: " + param);
//        } catch (Throwable throwable) {
//            logConcat.concat(throwable);
//            throw throwable;
//        } finally {
//            getAppOperationEventLog().save(workDetailPojo.getDeployEntity().getEventId(),
//                    AppOperationEventLogTypeEnum.APP_DEPLOY_FLOW_SET_UP, 3, logConcat.get());
//        }
    }

    private String addUserInfo(String uriStr, AppInfoWithCredential appInfo) {
//        try {
//            String userInfo = appInfo.getCredentialKey() + ":" + appInfo.getCredentialValue();
//            URI uri = new URI(uriStr);
//            return new URI(uri.getScheme(), userInfo, uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(),
//                    uri.getFragment()).toString();
//        } catch (Exception e) {
//            throw new BizException(DeployResultCodeConstants.GIT_URI_ERROR, e);
//        }
        return "";
    }
}
