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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 应用信息更新
 *
 * @author WangSongJun
 * @date 2019-07-16
 */
@Data
@ApiModel(description = "应用信息更新")
public class AppInfoUpdateFormParam {

    @Size(max = 20, message = "应用名称长度不能超过20")
    @Pattern(
            regexp = "^([a-zA-Z0-9\\u4e00-\\u9fa5]|[a-zA-Z0-9\\u4e00-\\u9fa5][a-zA-Z0-9\\u4e00-\\u9fa5\\-]*[a-zA-Z0-9\\u4e00-\\u9fa5])*([a-zA-Z0-9\\u4e00-\\u9fa5]|[a-zA-Z0-9\\u4e00-\\u9fa5][a-zA-Z0-9\\u4e00-\\u9fa5\\-]*[a-zA-Z0-9\\u4e00-\\u9fa5])$",
            message = "服务名只能由中文、字母（大小写）、数字、中横线组成"
    )
    @ApiModelProperty(value = "应用名称")
    private String appName;

    @Size(max = 500, message = "应用描述长度不能超过500")
    @ApiModelProperty(value = "应用描述")
    private String description;

    @ApiModelProperty(value = "负责人")
    private Long leaderId;
}
