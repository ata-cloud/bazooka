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
package net.atayun.bazooka.deploy.api.param;

import net.atayun.bazooka.base.enums.deploy.AppOperationEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * @author Ping
 * @date 2019-07-25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "服务操作")
public class AppOperationEventParam {

    @NotNull
    @ApiModelProperty("操作事件类型")
    private AppOperationEnum event;

    @NotNull
    @ApiModelProperty("服务Id")
    private Long appId;

    @NotNull
    @ApiModelProperty("环境id")
    private Long envId;

    @NotNull
    @ApiModelProperty("操作事件的参数(Json格式)")
    private String detail;
}