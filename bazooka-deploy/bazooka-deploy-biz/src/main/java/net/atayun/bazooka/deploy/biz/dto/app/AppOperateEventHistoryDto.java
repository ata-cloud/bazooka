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

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import net.atayun.bazooka.deploy.biz.constants.DeployConstants;
import net.atayun.bazooka.deploy.biz.enums.status.AppOperationEventStatusEnum;

import java.time.LocalDateTime;

/**
 * @author Ping
 * @date 2019-07-26
 */
@Getter
@Setter
@ApiModel(description = "服务事件操作记录dto")
public class AppOperateEventHistoryDto {

    @ApiModelProperty("事件Id")
    private Long eventId;

    @JsonFormat(pattern = DeployConstants.DATETIME_PATTERN)
    @ApiModelProperty("操作时间")
    private LocalDateTime operateDatetime;

    @ApiModelProperty("操作人")
    private String operator;

    @ApiModelProperty("事件")
    private String event;

    @ApiModelProperty("事件描述")
    private String remark;

    @ApiModelProperty("事件状态")
    private AppOperationEventStatusEnum status;
}
