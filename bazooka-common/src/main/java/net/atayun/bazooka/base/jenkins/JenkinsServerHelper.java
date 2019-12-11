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

import net.atayun.bazooka.base.config.JenkinsClientProperties;
import net.atayun.bazooka.base.dcos.DcosPropertiesHelper;
import net.atayun.bazooka.base.enums.code.OpsCommonExceptionCode;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpConnection;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.youyu.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author Ping
 * @date 2019-07-22
 */
@Slf4j
public class JenkinsServerHelper {

    private JenkinsClientProperties jenkinsClientProperties;

    @Autowired
    private DcosPropertiesHelper dcosPropertiesHelper;

    public JenkinsServerHelper(JenkinsClientProperties jenkinsClientProperties) {
        this.jenkinsClientProperties = jenkinsClientProperties;
    }

    private JenkinsServer jenkinsServer() {
        URI uri;
        try {
            uri = new URI(dcosPropertiesHelper.dcosMasterIp() + jenkinsClientProperties.getServerPath());
        } catch (URISyntaxException e) {
            throw new BizException(OpsCommonExceptionCode.JENKINS_URI_ERROR, e);
        }
        return new JenkinsServer(uri, jenkinsClientProperties.getUsername(), jenkinsClientProperties.getPasswordOrToken());
    }

    private JobWithDetails getJobWithDetails(String jobName) {
        JobWithDetails job;
        try {
            job = jenkinsServer().getJob(jobName);
        } catch (IOException e) {
            throw new BizException(OpsCommonExceptionCode.JENKINS_JOB_ERROR, e);
        }
        return job;
    }

    public void buildJob(String jobName, Map<String, String> param) {
        try {
            JobWithDetails job = getJobWithDetails(jobName);
            if (CollectionUtils.isEmpty(param)) {
                job.build(jenkinsClientProperties.getCrumbFlag());
            } else {
                job.build(param, jenkinsClientProperties.getCrumbFlag());
            }
        } catch (IOException e) {
            throw new BizException(OpsCommonExceptionCode.JENKINS_JOB_BUILD_ERROR, e);
        }
    }

    public String getJenkinsLog(String jobName, Integer buildNumber) {
        String consoleOutputText;
        try {
            JobWithDetails job = getJobWithDetails(jobName);
            Build build = job.getBuildByNumber(buildNumber);
            consoleOutputText = build.details().getConsoleOutputText();
        } catch (IOException e) {
            throw new BizException(OpsCommonExceptionCode.JENKINS_BUILD_DETAIL_ERROR, e);
        }
        return consoleOutputText;
    }

    public void cancel(String jobName, Integer buildNumber) {
        try {
            JobWithDetails job = getJobWithDetails(jobName);
            Build build = job.getBuildByNumber(buildNumber);
            if (build == null) {
                throw new BizException(OpsCommonExceptionCode.JENKINS_JOB_BUILD_ERROR, "取消构建异常");
            }
            JenkinsHttpConnection client = build.getClient();
            String url = build.getUrl();
            client.get(url + "stop");
        } catch (IOException e) {
            log.warn("取消jenkins发布异常", e);
        }
    }
}
