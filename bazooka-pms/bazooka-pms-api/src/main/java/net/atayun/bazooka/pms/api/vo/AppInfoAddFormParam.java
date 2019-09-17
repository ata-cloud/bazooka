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
package net.atayun.bazooka.pms.api.vo;

import net.atayun.bazooka.pms.api.enums.AppKindEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author WangSongJun
 * @date 2019-07-16
 */
@Data
@ApiModel(description = "应用信息")
public class AppInfoAddFormParam {

    @NotBlank(message = "应用名称不能空")
    @Size(max = 20, message = "应用名称长度不能超过20")
    @Pattern(
            regexp = "^([a-zA-Z0-9\\u4e00-\\u9fa5]|[a-zA-Z0-9\\u4e00-\\u9fa5][a-zA-Z0-9\\u4e00-\\u9fa5\\-]*[a-zA-Z0-9\\u4e00-\\u9fa5])*([a-zA-Z0-9\\u4e00-\\u9fa5]|[a-zA-Z0-9\\u4e00-\\u9fa5][a-zA-Z0-9\\u4e00-\\u9fa5\\-]*[a-zA-Z0-9\\u4e00-\\u9fa5])$",
            message = "服务名只能由中文、字母（大小写）、数字、中横线组成"
    )
    @ApiModelProperty(value = "应用名称")
    private String appName;

    @NotBlank(message = "应用Code不能空")
    @Size(max = 20, message = "应用Code长度不能超过20")
    @Pattern(
            regexp = "^(([a-z0-9]|[a-z0-9][a-z0-9\\-]*[a-z0-9])\\.)*([a-z0-9]|[a-z0-9][a-z0-9\\-]*[a-z0-9])$",
            message = "AppCode 必须至少为1个字符，并且只能包含数字（0-9），短划线（-），点（.）和小写字母（a-z）。名称不能以短划线开头或结尾。"
    )
    @ApiModelProperty(value = "应用代码")
    private String appCode;

    @Size(max = 500, message = "应用描述长度不能超过500")
    @ApiModelProperty(value = "应用描述")
    private String description;

    @NotNull(message = "负责人不能空")
    @ApiModelProperty(value = "负责人")
    private Long leaderId;

    @NotNull(message = "应用类型不能空")
    @ApiModelProperty(value = "应用类型")
    private AppKindEnum appKind;

    @ApiModelProperty(value = "git仓库地址")
    private String gitUrl;

    @ApiModelProperty("git仓库的凭据ID")
    private Long gitCredentialId;
}
