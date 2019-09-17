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
package net.atayun.bazooka.deploy.biz.dto.flow;

import net.atayun.bazooka.deploy.biz.enums.status.BasicStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Ping
 * @date 2019-08-16
 */
@Getter
@Setter
@ApiModel(description = "正在发布")
public class DeployingFlowResultDto {

    @ApiModelProperty("状态")
    private BasicStatusEnum status;

    @ApiModelProperty("流程")
    private List<DeployingFlowDto> flows;
}
