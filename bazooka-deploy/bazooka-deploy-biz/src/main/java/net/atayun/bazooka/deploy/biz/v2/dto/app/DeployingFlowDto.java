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
package net.atayun.bazooka.deploy.biz.v2.dto.app;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import net.atayun.bazooka.deploy.biz.v2.constant.DeployConstants;
import net.atayun.bazooka.deploy.biz.v2.enums.AppOptStatusEnum;

import java.time.LocalDateTime;

/**
 * @author Ping
 * @date 2019-07-24
 */
@Getter
@Setter
@ApiModel(description = "发布流dto")
public class DeployingFlowDto {

    @ApiModelProperty("发布Id")
    private Long deployId;

    @ApiModelProperty("发布流Id")
    private Long deployFlowId;

    @ApiModelProperty("发布流名称")
    private String displayName;

    @JsonFormat(pattern = DeployConstants.DATETIME_PATTERN)
    @ApiModelProperty("发布流完成时间")
    private LocalDateTime finishDatetime;

    @ApiModelProperty("发布流状态")
    private AppOptStatusEnum status;

    @ApiModelProperty("发布流log")
    private String log;
}
