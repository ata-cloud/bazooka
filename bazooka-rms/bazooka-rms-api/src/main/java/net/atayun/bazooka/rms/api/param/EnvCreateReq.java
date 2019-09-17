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
package net.atayun.bazooka.rms.api.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * @Author: hanxiaorui
 * @Date: 2019/7/16 17:54
 * @Description: 新增环境请求
 */
@Getter
@Setter
@ToString
public class EnvCreateReq {

    @ApiModelProperty("环境名称")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9\\-]{1,20}$", message = "环境名称不合法(必须为中文、字母大小写、数字、中横线且长度不超过20位)")
    private String envName;

    @ApiModelProperty("环境代码")
    @Pattern(regexp = "^[a-z0-9\\-]{1,20}$", message = "环境代码不合法(必须为字母小写、数字、中横线且长度不超过20位)")
    private String envCode;

    @ApiModelProperty("集群编号")
    @NotNull(message = "集群编号不能为空")
    private Long clusterId;

    @ApiModelProperty("CPU")
    @NotNull(message = "CPU不能为空")
    private BigDecimal cpus;

    @ApiModelProperty("内存")
    @NotNull(message = "内存不能为空")
    private BigDecimal memory;

    @ApiModelProperty("磁盘")
    @NotNull(message = "磁盘不能为空")
    private BigDecimal disk;
}
