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
package net.atayun.bazooka.base.enums.deploy;

/**
 * @author Ping
 * @date 2019-08-01
 */
public enum AppOperationEventLogTypeEnum {

    /**
     *
     */
    APP_DEPLOY,

    APP_DEPLOY_FLOW_SET_UP,

    APP_DEPLOY_FLOW_PULL_CODE,

    APP_DEPLOY_FLOW_BUILD_PROJECT,

    APP_DEPLOY_FLOW_BUILD_DOCKER_IMAGE,

    APP_DEPLOY_FLOW_DO_DEPLOY,

    APP_DEPLOY_FLOW_HEALTH_CHECK,

    DELETE_DOCKER_IMAGE,

    PUSH_DOCKER_IMAGE,

    APP_RESTART,

    APP_START,

    APP_SHUTDOWN,

    APP_SCALE,

    APP_ROLLBACK
}
