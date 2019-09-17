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
package net.atayun.bazooka.deploy.biz.param.jenkins;

import lombok.Getter;
import lombok.Setter;
import net.atayun.bazooka.deploy.biz.constants.JenkinsCallbackConstants;

import java.util.Map;

/**
 * @author Ping
 * @date 2019-07-26
 */
@Getter
@Setter
public class PushImageCallbackParam {

    public static final String CUSTOM_KEY_EVENT_ID = "eventId";
    public static final String CUSTOM_KEY_IMAGE_ID = "imageId";
    public static final String CUSTOM_KEY_TARGET_ENV_ID = "targetEnvId";

    private String jobName;

    private Integer jobBuildNumber;

    /**
     * SUCCESS,UNSTABLE,FAILURE,NOT_BUILT,ABORTED
     *
     * @see JenkinsCallbackConstants
     */
    private String result;

    private Map<String, String> custom;
}
