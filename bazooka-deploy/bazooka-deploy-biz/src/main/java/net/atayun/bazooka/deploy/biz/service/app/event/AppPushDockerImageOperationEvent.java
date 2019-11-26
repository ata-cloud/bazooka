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
package net.atayun.bazooka.deploy.biz.service.app.event;

import com.alibaba.fastjson.JSONObject;
import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.bean.SpringContextBean;
import net.atayun.bazooka.base.enums.deploy.AppOperationEnum;
import net.atayun.bazooka.base.enums.deploy.AppOperationEventLogTypeEnum;
import net.atayun.bazooka.base.jenkins.JenkinsJobPropertiesHelper;
import net.atayun.bazooka.base.jenkins.JenkinsServerHelper;
import net.atayun.bazooka.deploy.api.param.AppOperationEventParam;
import net.atayun.bazooka.deploy.biz.constants.JenkinsPushDockerImageJobConstants;
import net.atayun.bazooka.deploy.biz.enums.status.AppOperationEventStatusEnum;
import net.atayun.bazooka.deploy.biz.log.AppOperationEventLog;
import net.atayun.bazooka.deploy.biz.service.status.EventStatusOpt;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Ping
 * @date 2019-07-25
 * @see AppOperationEnum#PUSH_IMAGE
 */
@Component
@StrategyNum(superClass = AbstractAppOperationEvent.class,
        number = "PUSH_IMAGE",
        describe = "推送服务相关docker镜像"
)
public class AppPushDockerImageOperationEvent extends AbstractAppOperationEvent implements EventStatusOpt {

    @Autowired
    private JenkinsJobPropertiesHelper jenkinsJobPropertiesHelper;

    @Autowired
    private JenkinsServerHelper jenkinsServerHelper;

    @Autowired
    private AppApi appApi;

    @Autowired
    private EnvApi envApi;

    @Autowired
    private RmsDockerImageApi rmsDockerImageApi;

    @Autowired
    private PmsCredentialsApi pmsCredentialsApi;

    @Override
    public String getEventRemark(AppOperationEventParam appOperationEventParam) {
        AppPushDockerImageOperationEventPoJo appPushDockerImageOperationEventPoJo =
                JSONObject.parseObject(appOperationEventParam.getDetail(), AppPushDockerImageOperationEventPoJo.class);
        String targetDockerRegistry = getTargetDockerRegistry(appPushDockerImageOperationEventPoJo);
        return "目标镜像库: " + targetDockerRegistry + ", 镜像Tag: " + appPushDockerImageOperationEventPoJo.getDockerImageTag();
    }

    /**
     * 事件处理
     *
     * @param appOperationEventParam 参数
     * @param eventId                事件Id
     * @return subId
     */
    @Override
    public void doWork(AppOperationEventParam appOperationEventParam, Long eventId) {

        AppInfoDto appInfo = appApi.getAppInfoById(appOperationEventParam.getAppId())
                .ifNotSuccessThrowException()
                .getData();
        ClusterConfigDto clusterConfig = envApi.getEnvConfiguration(appOperationEventParam.getEnvId())
                .ifNotSuccessThrowException()
                .getData();
        AppPushDockerImageOperationEventPoJo pojo =
                JSONObject.parseObject(appOperationEventParam.getDetail(), AppPushDockerImageOperationEventPoJo.class);
        Boolean isExternalDockerRegistry = pojo.getIsExternalDockerRegistry();
        if (!isExternalDockerRegistry) {
            RmsDockerImagePushCheckResultDto data = rmsDockerImageApi.imagePushPreCheck(pojo.getImageId(), pojo.getTargetEnvId())
                    .ifNotSuccessThrowException()
                    .getData();
            boolean sameRegistry = data.isSameRegistry();
            if (sameRegistry) {
                rmsDockerImageApi.imageCopy(pojo.getImageId(), pojo.getTargetEnvId()).ifNotSuccessThrowException();
                updateEventStatus(eventId, AppOperationEventStatusEnum.SUCCESS);
                SpringContextBean.getBean(AppOperationEventLog.class).saveAndMerge(eventId, AppOperationEventLogTypeEnum.PUSH_DOCKER_IMAGE, "推送成功");
                return;
            }
        }

        String targetDockerRegistry = getTargetDockerRegistry(pojo);
        Map<String, String> param = new HashMap<>(JenkinsPushDockerImageJobConstants.PUSH_DOCKER_IMAGE_JOB_PARAM_COUNTS);
        param.put(JenkinsPushDockerImageJobConstants.SOURCE_DOCKER_REGISTRY, clusterConfig.getDockerHubUrl());
        param.put(JenkinsPushDockerImageJobConstants.TARGET_DOCKER_REGISTRY, targetDockerRegistry);
        Boolean needAuth = Optional.ofNullable(pojo.getNeedAuth()).orElse(false);
        param.put(JenkinsPushDockerImageJobConstants.NEED_AUTH, needAuth.toString());
        if (needAuth) {
            PmsCredentialsDto credentials = pmsCredentialsApi.getCredentialsDtoById(pojo.getCredentialId())
                    .ifNotSuccessThrowException()
                    .getData();
            param.put(JenkinsPushDockerImageJobConstants.USERNAME, credentials.getCredentialKey());
            param.put(JenkinsPushDockerImageJobConstants.PASSWORD, credentials.getCredentialValue());
        }
        param.put(JenkinsPushDockerImageJobConstants.DOCKER_IMAGE_NAME, appInfo.getDockerImageName());
        param.put(JenkinsPushDockerImageJobConstants.DOCKER_IMAGE_TAG, pojo.getDockerImageTag());
        param.put(JenkinsPushDockerImageJobConstants.PUSH_IMAGE_CALLBACK_URI, "");
        param.put(JenkinsPushDockerImageJobConstants.EVENT_ID, eventId.toString());
        param.put(JenkinsPushDockerImageJobConstants.IMAGE_ID, pojo.getImageId().toString());
        param.put(JenkinsPushDockerImageJobConstants.TARGET_ENV_ID, Optional.ofNullable(pojo.getTargetEnvId()).map(Object::toString).orElse(""));
        String dockerImageJobName = jenkinsJobPropertiesHelper.randomPushDockerImageJobName();
        jenkinsServerHelper.buildJob(dockerImageJobName, param);
    }

    private String getTargetDockerRegistry(AppPushDockerImageOperationEventPoJo appPushDockerImageOperationEventPoJo) {
//        String targetDockerRegistry;
//        if (appPushDockerImageOperationEventPoJo.getIsExternalDockerRegistry()) {
//            targetDockerRegistry = appPushDockerImageOperationEventPoJo.getTargetDockerRegistry();
//        } else {
//            targetDockerRegistry = envApi.getEnvConfiguration(appPushDockerImageOperationEventPoJo.getTargetEnvId())
//                    .ifNotSuccessThrowException()
//                    .getData()
//                    .getDockerHubUrl();
//        }
//        if (targetDockerRegistry.startsWith(CommonConstants.PROTOCOL)) {
//            targetDockerRegistry = targetDockerRegistry.replace(CommonConstants.PROTOCOL, "");
//        }
//        if (targetDockerRegistry.startsWith(CommonConstants.SECURE_PROTOCOL)) {
//            targetDockerRegistry = targetDockerRegistry.replace(CommonConstants.SECURE_PROTOCOL, "");
//        }
//        return targetDockerRegistry;
        return "";
    }
}
