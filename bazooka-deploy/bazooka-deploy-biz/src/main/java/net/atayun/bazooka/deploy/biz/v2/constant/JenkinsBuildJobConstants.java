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
package net.atayun.bazooka.deploy.biz.v2.constant;

/**
 * @author Ping
 * @date 2019-07-31
 */
public final class JenkinsBuildJobConstants {

    public static final String OPT_ID = "OPT_ID";
    public static final String STEP_ID = "STEP_ID";
    public static final String WORK_PATH = "WORK_PATH";

    private JenkinsBuildJobConstants() {
        throw new AssertionError("Must not instantiate constant utility class");
    }

    /**
     * 参数个数
     */
    public static final Integer BUILD_JOB_PARAM_COUNTS = 16;

    /**
     * 服务Gitlab仓库地址
     */
    public static final String GIT_REPOSITORY_URL = "GIT_REPOSITORY_URL";

    /**
     * 服务名称
     */
    public static final String SERVICE_NAME = "SERVICE_NAME";

    /**
     * 选择特定分支进行构建
     */
    public static final String BRANCH = "BRANCH";

    /**
     * 构建脚本中的回调
     */
    public static final String BUILD_SCRIPT_CALLBACK_URI = "BUILD_SCRIPT_CALLBACK_URI";

    /**
     * Ata服务事件ID
     */
    public static final String EVENT_ID = "EVENT_ID";

    /**
     * Ata服务发布记录ID
     */
    public static final String DEPLOY_ID = "DEPLOY_ID";

    /**
     * 代码编译命令(发布配置中设置)
     */
    public static final String COMPILE_COMMAND = "COMPILE_COMMAND";

    /**
     * 镜像库地址
     */
    public static final String DOCKER_REGISTRY = "DOCKER_REGISTRY";

    /**
     * 镜像名称
     */
    public static final String DOCKER_IMAGE_NAME = "DOCKER_IMAGE_NAME";

    /**
     * 镜像Tag
     */
    public static final String DOCKER_IMAGE_TAG = "DOCKER_IMAGE_TAG";

    /**
     * Dockerfile相对路径(发布配置中设置)
     */
    public static final String DOCKERFILE_PATH = "DOCKERFILE_PATH";

    /**
     * 构建回调URI
     */
    public static final String BUILD_CALLBACK_URI = "BUILD_CALLBACK_URI";
}
