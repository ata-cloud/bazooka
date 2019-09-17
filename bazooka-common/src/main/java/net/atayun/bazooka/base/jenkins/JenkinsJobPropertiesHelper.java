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
package net.atayun.bazooka.base.jenkins;

import net.atayun.bazooka.base.config.JenkinsJobProperties;
import net.atayun.bazooka.base.dcos.DcosPropertiesHelper;
import com.youyu.common.exception.BizException;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author Ping
 * @date 2019-07-23
 */
public class JenkinsJobPropertiesHelper {

    @Value("${server.port}")
    private Integer serverPort;

    private JenkinsJobProperties jenkinsJobProperties;

    @Autowired
    private DcosPropertiesHelper dcosPropertiesHelper;

    public JenkinsJobPropertiesHelper(JenkinsJobProperties jenkinsJobProperties) {
        this.jenkinsJobProperties = jenkinsJobProperties;
    }

    public String randomBuildJobName() {
        List<String> jenkinsBuildJobs = jenkinsJobProperties.getJenkinsBuildJobs();
        if (CollectionUtils.isEmpty(jenkinsBuildJobs)) {
            throw new BizException("", "未配置任何构建Job");
        }
        return jenkinsBuildJobs.get(RandomUtils.nextInt(0, jenkinsBuildJobs.size()));
    }

    public String randomPushDockerImageJobName() {
        List<String> jenkinsPushDockerImageJobs = jenkinsJobProperties.getJenkinsPushDockerImageJobs();
        if (CollectionUtils.isEmpty(jenkinsPushDockerImageJobs)) {
            throw new BizException("", "未配置任何推送镜像Job");
        }
        return jenkinsPushDockerImageJobs.get(RandomUtils.nextInt(0, jenkinsPushDockerImageJobs.size()));
    }

    public String buildScriptCallbackUri() {
        return dcosPropertiesHelper.dcosPublicAgentIp() + ":" + serverPort + jenkinsJobProperties.getBuildScriptCallbackPath();
    }

    public String buildCallbackUri() {
        return dcosPropertiesHelper.dcosPublicAgentIp() + ":" + serverPort + jenkinsJobProperties.getBuildCallbackPath();
    }

    public String pushImageCallbackUri() {
        return dcosPropertiesHelper.dcosPublicAgentIp() + ":" + serverPort + jenkinsJobProperties.getPushImageCallbackPath();
    }
}
