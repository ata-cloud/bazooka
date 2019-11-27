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

import com.youyu.common.exception.BizException;
import net.atayun.bazooka.base.config.JenkinsJobProperties;
import net.atayun.bazooka.base.dcos.DcosPropertiesHelper;
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

    public String randomPullCode() {
        return randomJob(jenkinsJobProperties.getPullCodeJobs());
    }

    public String randomPackageProject() {
        return randomJob(jenkinsJobProperties.getPackageProjectJobs());
    }

    public String randomBuildDockerImage() {
        return randomJob(jenkinsJobProperties.getBuildImageJobs());
    }

    public String randomPushDockerImageJobName() {
        return randomJob(jenkinsJobProperties.getPushImageJobs());
    }

    private String randomJob(List<String> jobs) {
        if (CollectionUtils.isEmpty(jobs)) {
            throw new BizException("", "未配置任何Job");
        }
        return jobs.get(RandomUtils.nextInt(0, jobs.size()));
    }

    public String buildCallbackUri() {
//        return dcosPropertiesHelper.dcosPublicAgentIp() + ":" + serverPort + jenkinsJobProperties.getBuildCallbackPath();
        return "http://10.0.52.50:" + serverPort + jenkinsJobProperties.getBuildCallbackPath();
    }

}
