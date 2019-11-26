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
package net.atayun.bazooka.deploy.biz.enums.flow;

import lombok.Getter;
import net.atayun.bazooka.base.enums.deploy.AppOperationEventLogTypeEnum;

/**
 * @author Ping
 * @date 2019-07-10
 */
public enum DeployFlowEnum {

    /**
     * DeployFlowEnum
     */
    SET_UP("开始", AppOperationEventLogTypeEnum.APP_DEPLOY_FLOW_SET_UP),

    PULL_CODE("拉取代码", AppOperationEventLogTypeEnum.APP_DEPLOY_FLOW_PULL_CODE),

    BUILD_PROJECT("编译", AppOperationEventLogTypeEnum.APP_DEPLOY_FLOW_BUILD_PROJECT),

    BUILD_DOCKER_IMAGE("构建镜像", AppOperationEventLogTypeEnum.APP_DEPLOY_FLOW_BUILD_DOCKER_IMAGE),

    DO_DEPLOY("发布", AppOperationEventLogTypeEnum.APP_DEPLOY_FLOW_DO_DEPLOY),

    HEALTH_CHECK("健康检查", AppOperationEventLogTypeEnum.APP_DEPLOY_FLOW_HEALTH_CHECK);

    private String description;

    @Getter
    private AppOperationEventLogTypeEnum logType;

    DeployFlowEnum(String description, AppOperationEventLogTypeEnum logType) {
        this.description = description;
        this.logType = logType;
    }

    public String getDescription() {
        return description;
    }

    public String lowerCase() {
        return this.name().toLowerCase();
    }


    //下列常量需与枚举实例保持一致

    public static final String FLOW_SET_UP = "SET_UP";
    public static final String FLOW_PULL_CODE = "PULL_CODE";
    public static final String FLOW_BUILD_PROJECT = "BUILD_PROJECT";
    public static final String FLOW_BUILD_DOCKER_IMAGE = "BUILD_DOCKER_IMAGE";
    public static final String FLOW_DO_DEPLOY = "DO_DEPLOY";
    public static final String FLOW_HEALTH_CHECK = "HEALTH_CHECK";
}
