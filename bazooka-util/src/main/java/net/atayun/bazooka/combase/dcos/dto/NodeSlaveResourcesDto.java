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
package net.atayun.bazooka.combase.dcos.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月18日 17:00:00
 * @work dcos slave资源信息
 */
@Setter
@Getter
public class NodeSlaveResourcesDto implements Serializable {

    private static final long serialVersionUID = 954895725379203427L;

    @ApiModelProperty("磁盘")
    private BigDecimal disk;

    @ApiModelProperty("内存")
    private BigDecimal mem;

    @ApiModelProperty("cpu")
    private BigDecimal cpus;
}
