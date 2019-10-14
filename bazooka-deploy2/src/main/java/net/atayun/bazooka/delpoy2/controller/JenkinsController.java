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
package net.atayun.bazooka.delpoy2.controller;

import net.atayun.bazooka.deploy.biz.param.jenkins.BuildCallbackParam;
import net.atayun.bazooka.deploy.biz.param.jenkins.BuildScriptCallbackParam;
import net.atayun.bazooka.deploy.biz.param.jenkins.PushImageCallbackParam;
import net.atayun.bazooka.deploy.biz.service.jenkins.JenkinsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Ping
 * @date 2019-07-22
 */
@Controller
@RequestMapping("/deploy/jenkins")
public class JenkinsController {

    @Autowired
    private JenkinsService jenkinsService;

    @PostMapping("/build/script/callback")
    public void buildScriptCallback(@Validated @RequestBody BuildScriptCallbackParam buildScriptCallbackParam) {
        jenkinsService.buildScriptCallback(buildScriptCallbackParam);
    }

    @PostMapping("/build/callback")
    public void buildCallback(@Validated @RequestBody BuildCallbackParam buildCallbackParam) {
        jenkinsService.buildCallback(buildCallbackParam);
    }

    @PostMapping("/push-image/callback")
    public void pushImageCallback(@Validated @RequestBody PushImageCallbackParam pushImageCallbackParam) {
        jenkinsService.pushImageCallback(pushImageCallbackParam);
    }
}
