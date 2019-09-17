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
package net.atayun.bazooka.deploy.biz.dto.app;

import net.atayun.bazooka.deploy.biz.enums.status.AppOperationEventStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author Ping
 * @date 2019-07-26
 */
@Getter
@Setter
@ApiModel(description = "Marathon相关事件dto")
public class AppOperateEventHistoryMarathonDto {

    @ApiModelProperty("事件Id")
    private Long eventId;

    @ApiModelProperty("事件")
    private String event;

    @ApiModelProperty("Marathon发布版本")
    private String version;

    @ApiModelProperty("镜像标签")
    private String imageTag;

    @ApiModelProperty("git commit")
    private String gitCommitId;

    @ApiModelProperty("git commit time")
    private LocalDateTime gitCommitTime;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("状态")
    private AppOperationEventStatusEnum statusCode;

    @ApiModelProperty("是否是最新操作")
    private Boolean isTheLast;

    @ApiModelProperty("镜像是否被删除")
    private Boolean imageIsDelete;

    @ApiModelProperty("是否在回滚")
    private Boolean isRollback;
}
