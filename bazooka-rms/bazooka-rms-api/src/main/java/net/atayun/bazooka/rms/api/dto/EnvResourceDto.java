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
package net.atayun.bazooka.rms.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

/**
 * 环境资源信息
 *
 * @author 技术平台
 * @date 2019-07-16
 */
@Data
@ApiModel
public class EnvResourceDto {

    @ApiModelProperty("环境编号")
    private Long id;

    @ApiModelProperty("环境代码")
    private String code;

    @ApiModelProperty("环境名称")
    private String name;

    @ApiModelProperty("CPU")
    private BigDecimal cpus = ZERO;

    @ApiModelProperty("内存")
    private BigDecimal memory = ZERO;

    @ApiModelProperty("磁盘")
    private BigDecimal disk = ZERO;
}
