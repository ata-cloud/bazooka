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
package net.atayun.bazooka.deploy.biz.constants;

/**
 * @author Ping
 * @date 2019-07-31
 */
public final class JenkinsPushDockerImageJobConstants {

    private JenkinsPushDockerImageJobConstants() {
        throw new AssertionError("Must not instantiate constant utility class");
    }

    /**
     * 参数个数
     */
    public static final Integer PUSH_DOCKER_IMAGE_JOB_PARAM_COUNTS = 11;

    /**
     * 源镜像库地址
     */
    public static final String SOURCE_DOCKER_REGISTRY = "SOURCE_DOCKER_REGISTRY";

    /**
     * 目标镜像库地址
     */
    public static final String TARGET_DOCKER_REGISTRY = "TARGET_DOCKER_REGISTRY";

    /**
     * 是否需要认证
     */
    public static final String NEED_AUTH = "NEED_AUTH";

    /**
     * 用户名
     */
    public static final String USERNAME = "USERNAME";

    /**
     * 密码
     */
    public static final String PASSWORD = "PASSWORD";

    /**
     * 镜像名称
     */
    public static final String DOCKER_IMAGE_NAME = "DOCKER_IMAGE_NAME";

    /**
     * 镜像Tag
     */
    public static final String DOCKER_IMAGE_TAG = "DOCKER_IMAGE_TAG";

    /**
     * 推送镜像完成后的回调
     */
    public static final String PUSH_IMAGE_CALLBACK_URI = "PUSH_IMAGE_CALLBACK_URI";

    /**
     * Ata服务事件ID
     */
    public static final String EVENT_ID = "EVENT_ID";

    public static final String IMAGE_ID = "IMAGE_ID";

    public static final String TARGET_ENV_ID = "TARGET_ENV_ID";
}
