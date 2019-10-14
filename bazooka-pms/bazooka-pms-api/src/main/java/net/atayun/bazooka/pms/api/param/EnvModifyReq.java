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
package net.atayun.bazooka.pms.api.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * @Author: hanxiaorui
 * @Date: 2019/7/16 17:54
 * @Description:
 */
@Getter
@Setter
@ToString
public class EnvModifyReq {

    @ApiModelProperty("环境编号")
    @Positive(message = "环境编号不能为空")
    private Long envId;

    @ApiModelProperty("集群编号")
    private Long clusterId;

    @ApiModelProperty("环境状态(0:正常使用,1:暂停发布)")
    private String envState;

    @ApiModelProperty("CPU")
    private BigDecimal cpus;

    @ApiModelProperty("内存")
    private BigDecimal memory;

    @ApiModelProperty("磁盘")
    private BigDecimal disk;
}
