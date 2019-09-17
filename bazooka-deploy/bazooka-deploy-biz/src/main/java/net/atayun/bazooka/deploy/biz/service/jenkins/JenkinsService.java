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
package net.atayun.bazooka.deploy.biz.service.jenkins;

import net.atayun.bazooka.deploy.biz.param.jenkins.BuildCallbackParam;
import net.atayun.bazooka.deploy.biz.param.jenkins.BuildScriptCallbackParam;
import net.atayun.bazooka.deploy.biz.param.jenkins.PushImageCallbackParam;

/**
 * @author Ping
 * @date 2019-07-22
 */
public interface JenkinsService {

    /**
     * Job构建脚本执过程中的回调
     *
     * @param buildScriptCallbackParam 请求值
     */
    void buildScriptCallback(BuildScriptCallbackParam buildScriptCallbackParam);

    /**
     * Job构建完成后回调
     *
     * @param buildCallbackParam 请求值
     */
    void buildCallback(BuildCallbackParam buildCallbackParam);

    /**
     * 推送镜像回调
     *
     * @param pushImageCallbackParam pushImageCallbackParam
     */
    void pushImageCallback(PushImageCallbackParam pushImageCallbackParam);
}
