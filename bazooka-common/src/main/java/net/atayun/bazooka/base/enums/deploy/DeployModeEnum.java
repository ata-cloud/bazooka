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
 * 暂支持两种发布模式[构建, 镜像]
 *
 * @author Ping
 * @date 2019-07-12
 */
public enum DeployModeEnum {

    /**
     * 构建发布
     */
    BUILD,

    /**
     * 镜像发布
     */
    DOCKER_IMAGE,

    NODE_BUILD,

    MARATHON_BUILD,

    KUBERNETES_BUILD;

    //下列常量需与枚举实例保持一致

    public static final String MODE_BUILD = "BUILD";
    public static final String MODE_DOCKER_IMAGE = "DOCKER_IMAGE";
    public static final String MODE_NODE_BUILD = "NODE_BUILD";
    public static final String MODE_MARATHON_BUILD = "MARATHON_BUILD";
    public static final String MODE_KUBERNETES_BUILD = "KUBERNETES_BUILD";
}
