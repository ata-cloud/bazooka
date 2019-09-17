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
 * 应用相关操作类型
 *
 * @author WangSongJun
 * @date 2019-07-25
 */
public enum AppOperationEnum {

    /**
     *
     */
    START("启动服务", AppOperationEventLogTypeEnum.APP_START, true),
    RESTART("重启服务", AppOperationEventLogTypeEnum.APP_RESTART, true),
    SCALE("扩/缩容服务", AppOperationEventLogTypeEnum.APP_SCALE, true),
    STOP("关闭服务", AppOperationEventLogTypeEnum.APP_SHUTDOWN, true),

    DEPLOY("发布", AppOperationEventLogTypeEnum.APP_DEPLOY, true),
    ROLLBACK("回滚", AppOperationEventLogTypeEnum.APP_ROLLBACK, true),

    PUSH_IMAGE("镜像推送", AppOperationEventLogTypeEnum.PUSH_DOCKER_IMAGE, false),
    DELETE_IMAGE("删除镜像", AppOperationEventLogTypeEnum.DELETE_DOCKER_IMAGE, false);

    private String description;

    private AppOperationEventLogTypeEnum logType;

    private Boolean appRunStatus;

    AppOperationEnum(String description, AppOperationEventLogTypeEnum logType, Boolean appRunStatus) {
        this.description = description;
        this.logType = logType;
        this.appRunStatus = appRunStatus;
    }

    public String getDescription() {
        return description;
    }

    public AppOperationEventLogTypeEnum getLogType() {
        return logType;
    }

    public Boolean getAppRunStatus() {
        return appRunStatus;
    }
}
