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
package net.atayun.bazooka.deploy.biz.dto.deploy;

import com.youyu.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import net.atayun.bazooka.deploy.biz.enums.status.BasicStatusEnum;

/**
 * @author Ping
 * @date 2019-07-11
 */
@Getter
@Setter
@ApiModel(description = "发布dto")
public class DeployDto extends BaseDto<Long> {

    @ApiModelProperty("发布Id")
    private Long deployId;

    @ApiModelProperty("发布配置id")
    private Long deployConfigId;

    @ApiModelProperty("分支")
    private String branch;

    @ApiModelProperty("镜像tag")
    private String dockerImageTag;

    @ApiModelProperty("发布状态")
    private BasicStatusEnum status;

    @ApiModelProperty("事件Id")
    private Long eventId;

}
