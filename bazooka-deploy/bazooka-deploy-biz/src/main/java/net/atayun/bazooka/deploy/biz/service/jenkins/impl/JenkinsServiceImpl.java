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
package net.atayun.bazooka.deploy.biz.service.jenkins.impl;

import com.alibaba.fastjson.JSONObject;
import com.youyu.common.enums.IsDeleted;
import com.youyu.common.exception.BizException;
import net.atayun.bazooka.combase.enums.deploy.AppOperationEventLogTypeEnum;
import net.atayun.bazooka.combase.JenkinsServerHelper;
import net.atayun.bazooka.deploy.biz.constants.JenkinsCallbackConstants;
import net.atayun.bazooka.deploy.biz.dal.entity.deploy.DeployEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowImageEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowJenkinsEntity;
import net.atayun.bazooka.deploy.biz.enums.flow.DeployFlowEnum;
import net.atayun.bazooka.deploy.biz.enums.flow.DispatchFlowSourceEnum;
import net.atayun.bazooka.deploy.biz.enums.status.AppOperationEventStatusEnum;
import net.atayun.bazooka.deploy.biz.enums.status.BasicStatusEnum;
import net.atayun.bazooka.deploy.biz.log.AppOperationEventLog;
import net.atayun.bazooka.deploy.biz.log.LogConcat;
import net.atayun.bazooka.deploy.biz.param.jenkins.BuildCallbackParam;
import net.atayun.bazooka.deploy.biz.param.jenkins.BuildScriptCallbackParam;
import net.atayun.bazooka.deploy.biz.param.jenkins.GitCommit;
import net.atayun.bazooka.deploy.biz.param.jenkins.PushImageCallbackParam;
import net.atayun.bazooka.deploy.biz.service.deploy.DeployService;
import net.atayun.bazooka.deploy.biz.service.deploy.event.DispatchFlowEvent;
import net.atayun.bazooka.deploy.biz.service.deploy.event.DispatchFlowEventPojo;
import net.atayun.bazooka.deploy.biz.service.flow.DeployFlowImageService;
import net.atayun.bazooka.deploy.biz.service.flow.DeployFlowJenkinsService;
import net.atayun.bazooka.deploy.biz.service.flow.DeployFlowService;
import net.atayun.bazooka.deploy.biz.service.jenkins.JenkinsService;
import net.atayun.bazooka.deploy.biz.service.status.AppStatusOpt;
import net.atayun.bazooka.deploy.biz.service.status.EventStatusOpt;
import net.atayun.bazooka.pms.api.dto.AppInfoDto;
import net.atayun.bazooka.pms.api.feign.AppApi;
import net.atayun.bazooka.pms.api.RmsDockerImageApi;
import net.atayun.bazooka.pms.api.api.EnvApi;
import net.atayun.bazooka.pms.api.dto.ClusterConfigDto;
import net.atayun.bazooka.pms.api.dto.RmsDockerImageDto;
import net.atayun.bazooka.pms.api.enums.DockerImageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static net.atayun.bazooka.deploy.biz.constants.DeployResultCodeConstants.BUILD_SCRIPT_CALLBACK_ERR_CODE;

/**
 * @author Ping
 * @date 2019-07-22
 */
@Service
public class JenkinsServiceImpl implements JenkinsService, AppStatusOpt, EventStatusOpt {

    @Autowired
    private DeployService deployService;

    @Autowired
    private DeployFlowService deployFlowService;

    @Autowired
    private DeployFlowJenkinsService deployFlowJenkinsService;

    @Autowired
    private DeployFlowImageService deployFlowImageService;

    @Autowired
    private RmsDockerImageApi rmsDockerImageApi;

    @Autowired
    private AppApi appApi;

    @Autowired
    private EnvApi envApi;

    @Autowired
    private JenkinsServerHelper jenkinsServerHelper;

    @Autowired
    private AppOperationEventLog appOperationEventLog;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Job构建脚本执过程中的回调
     *
     * @param param 请求值
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void buildScriptCallback(BuildScriptCallbackParam param) {
        BasicStatusEnum status = BasicStatusEnum.SUCCESS;
        Long deployId = param.getDeployId();
        DeployFlowEnum deployFlowType = param.getDeployFlowType();
        DeployFlowEntity deployFlowEntity = deployFlowService.selectByDeployIdAndFlowType(deployId, deployFlowType);
        try {
            Long deployFlowId = deployFlowEntity.getId();
            //Jenkins信息
            DeployFlowJenkinsEntity deployFlowJenkinsEntity = new DeployFlowJenkinsEntity();
            deployFlowJenkinsEntity.setDeployFlowId(deployFlowId);
            deployFlowJenkinsEntity.setJobName(param.getJenkinsJobName());
            deployFlowJenkinsEntity.setJobBuildNumber(param.getJenkinsJobBuildNumber());
            deployFlowJenkinsService.insertEntity(deployFlowJenkinsEntity);

            //镜像信息
            if (DeployFlowEnum.BUILD_DOCKER_IMAGE == deployFlowType) {
                saveImageInfo(param, deployFlowEntity);
            }
        } catch (Throwable throwable) {
            status = BasicStatusEnum.FAILURE;
            throw new BizException(BUILD_SCRIPT_CALLBACK_ERR_CODE, String.format("构建回调失败[%s]", deployFlowType), throwable);
        } finally {
            LogConcat logConcat = getJenkinsBuildScriptCallbackLogConcat(param.getJenkinsJobName(), param.getJenkinsJobBuildNumber(), param.getDeployFlowType());
            appOperationEventLog.save(param.getEventId(), deployFlowType.getLogType(), 2, logConcat.get());
            Integer flowNumber = deployFlowEntity.getFlowNumber();

            BasicStatusEnum finalStatus = status;
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    DispatchFlowEventPojo eventPojo = new DispatchFlowEventPojo(deployId, flowNumber, finalStatus);
                    applicationContext.publishEvent(new DispatchFlowEvent(DispatchFlowSourceEnum.JENKINS_CALLBACK, eventPojo));
                }
            });
        }
    }

    private void saveImageInfo(BuildScriptCallbackParam param, DeployFlowEntity deployFlowEntity) {
        DeployEntity deployEntity = deployService.selectEntityById(deployFlowEntity.getDeployId());
        AppInfoDto appInfo = appApi.getAppInfoById(deployEntity.getAppId())
                .ifNotSuccessThrowException()
                .getData();
        String dockerImageName = appInfo.getDockerImageName();
        String dockerImageTag = param.getDockerImageTag();
        DeployFlowImageEntity deployFlowImageEntity = new DeployFlowImageEntity();
        deployFlowImageEntity.setDeployFlowId(deployFlowEntity.getId());
        deployFlowImageEntity.setDockerImageName(dockerImageName);
        deployFlowImageEntity.setDockerImageTag(dockerImageTag);
        deployFlowImageService.insertEntity(deployFlowImageEntity);

        ClusterConfigDto clusterConfig = envApi.getEnvConfiguration(deployEntity.getEnvId())
                .ifNotSuccessThrowException()
                .getData();
        String branch = deployEntity.getBranch();
        GitCommit gitCommit = JSONObject.parseObject(param.getGitCommit(), GitCommit.class);
        RmsDockerImageDto rmsDockerImageDto = new RmsDockerImageDto();
        rmsDockerImageDto.setEnvId(deployEntity.getEnvId());
        rmsDockerImageDto.setAppId(deployEntity.getAppId());
        rmsDockerImageDto.setRegistry(clusterConfig.getDockerHubUrl());
        rmsDockerImageDto.setImageName(dockerImageName);
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
        rmsDockerImageApi.create(rmsDockerImageDto);
    }

    private LogConcat getJenkinsBuildScriptCallbackLogConcat(String jobName, Integer buildNumber, DeployFlowEnum flow) {
        final LogConcat logConcat = new LogConcat(">> 2. Jenkins回调");
        logConcat.concat("Job: " + jobName);
        logConcat.concat("BuildNumber: " + buildNumber + "\n\n");
        logConcat.concat(">> 3. Jenkins详细信息");
        String jenkinsLog = jenkinsServerHelper.getJenkinsLog(jobName, buildNumber);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(jenkinsLog.getBytes())));
        try {
            if (flow == DeployFlowEnum.PULL_CODE) {
                while (true) {
                    String len = bufferedReader.readLine();
                    if (len == null || Objects.equals("<<<<<ATA_BUILD_PULL_CODE_FINISH", len)) {
                        break;
                    }
                    logConcat.concat(len);
                }
            } else if (flow == DeployFlowEnum.BUILD_PROJECT) {
                String len;
                do {
                    len = bufferedReader.readLine();
                } while (len != null && !Objects.equals("<<<<<ATA_BUILD_PULL_CODE_FINISH", len));
                if (len != null) {
                    while (true) {
                        len = bufferedReader.readLine();
                        if (len == null || Objects.equals("<<<<<ATA_BUILD_BUILD_PROJECT_FINISH", len)) {
                            break;
                        }
                        logConcat.concat(len);
                    }
                }
            } else if (flow == DeployFlowEnum.BUILD_DOCKER_IMAGE) {
                String len;
                do {
                    len = bufferedReader.readLine();
                } while (len != null && !Objects.equals("<<<<<ATA_BUILD_BUILD_PROJECT_FINISH", len));
                if (len != null) {
                    while (true) {
                        len = bufferedReader.readLine();
                        if (len == null || Objects.equals("<<<<<ATA_BUILD_DOCKER_IMAGE_FINISH", len)) {
                            break;
                        }
                        logConcat.concat(len);
                    }
                }
            }
        } catch (IOException e) {
            //...
        }
        return logConcat;
    }

    /**
     * Job构建完成后回调
     *
     * @param buildCallbackParam 请求值
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void buildCallback(BuildCallbackParam buildCallbackParam) {
        String buildResult = buildCallbackParam.getResult();
        if (Objects.equals(buildResult, JenkinsCallbackConstants.BUILD_RESULT_SUCCESS)) {
            return;
        }
        String jobName = buildCallbackParam.getJobName();
        Integer buildNumber = buildCallbackParam.getJobBuildNumber();
        Map<String, String> custom = buildCallbackParam.getCustom();
        Long deployId = Long.parseLong(custom.get(BuildCallbackParam.CUSTOM_KEY_DEPLOY_ID));
        Long eventId = Long.parseLong(custom.get(BuildCallbackParam.CUSTOM_KEY_EVENT_ID));
        List<DeployFlowJenkinsEntity> deployFlowJenkinsEntities = deployFlowJenkinsService.selectByJobInfoOrderByDeployFlowId(jobName, buildNumber);
        Long deployFlowId;
        if (CollectionUtils.isEmpty(deployFlowJenkinsEntities)) {
            DeployFlowEntity deployFlowEntity = deployFlowService.selectByDeployIdAndFlowType(deployId, DeployFlowEnum.PULL_CODE);
            deployFlowId = deployFlowEntity.getId();
        } else {
            DeployFlowJenkinsEntity deployFlowJenkinsEntity = deployFlowJenkinsEntities.get(deployFlowJenkinsEntities.size() - 1);
            DeployFlowEntity deployFlowEntity = deployFlowService.selectById(deployFlowJenkinsEntity.getDeployFlowId());
            deployFlowEntity = deployFlowService.selectByDeployIdAndFlowNumber(deployId, deployFlowEntity.getFlowNumber() + 1);
            deployFlowId = deployFlowEntity.getId();
        }
        DeployFlowEntity deployFlowEntity = deployFlowService.selectById(deployFlowId);
        DeployFlowEnum flowType = deployFlowEntity.getFlowType();
        LogConcat logConcat = getJenkinsBuildScriptCallbackLogConcat(jobName, buildNumber, flowType);
        appOperationEventLog.save(eventId, flowType.getLogType(), 2, logConcat.get());
        Integer flowNumber = deployFlowEntity.getFlowNumber();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                DispatchFlowEventPojo eventPojo = new DispatchFlowEventPojo(deployId, flowNumber, BasicStatusEnum.FAILURE);
                applicationContext.publishEvent(new DispatchFlowEvent(DispatchFlowSourceEnum.JENKINS_CALLBACK, eventPojo));
            }
        });
    }

    /**
     * 推送镜像回调
     *
     * @param pushImageCallbackParam pushImageCallbackParam
     */
    @Override
    public void pushImageCallback(PushImageCallbackParam pushImageCallbackParam) {
        Map<String, String> custom = pushImageCallbackParam.getCustom();
        //copy 镜像
        String targetEnvIdStr = custom.get(PushImageCallbackParam.CUSTOM_KEY_TARGET_ENV_ID);
        if (StringUtils.hasText(targetEnvIdStr)) {
            Long targetEnvId = Long.parseLong(targetEnvIdStr);
            Long imageId = Long.parseLong(custom.get(PushImageCallbackParam.CUSTOM_KEY_IMAGE_ID));
            rmsDockerImageApi.imageCopy(imageId, targetEnvId).ifNotSuccessThrowException();
        }

        String eventIdStr = custom.get(PushImageCallbackParam.CUSTOM_KEY_EVENT_ID);
        long eventId = Long.parseLong(eventIdStr);

        AppOperationEventLogTypeEnum pushDockerImage = AppOperationEventLogTypeEnum.PUSH_DOCKER_IMAGE;
        String jenkinsLog = jenkinsServerHelper.getJenkinsLog(pushImageCallbackParam.getJobName(), pushImageCallbackParam.getJobBuildNumber());
        appOperationEventLog.saveAndMerge(eventId, pushDockerImage, 2, jenkinsLog);

        AppOperationEventStatusEnum status =
                Objects.equals(pushImageCallbackParam.getResult(), JenkinsCallbackConstants.BUILD_RESULT_SUCCESS) ?
                        AppOperationEventStatusEnum.SUCCESS :
                        AppOperationEventStatusEnum.FAILURE;

        appOperationEventLog.save(eventId, pushDockerImage, "镜像推送结果:\n状态:" + status);
        updateEventStatus(eventId, status);
        updateAppStatus(eventId, false);
    }

}
