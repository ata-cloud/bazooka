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
package net.atayun.bazooka.pms.api.dto.rsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 集群节点响应 Dto
 */
@Setter
@Getter
@ApiModel
public class ClusterNodeRspDto implements Serializable {

    private static final long serialVersionUID = 7863557465226887741L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("节点id")
    private String nodeId;

    @ApiModelProperty("ip")
    private String ip;

    @ApiModelProperty("节点类型")
    private String nodeType;

    @ApiModelProperty("状态 0:健康(绿色) 1:异常(红色) -1:没有(灰色)")
    private String status;

    @ApiModelProperty("容器数量")
    private Integer containerQuantity;

    @ApiModelProperty("总cpu信息")
    private BigDecimal cpu;

    @ApiModelProperty("总内存信息")
    private BigDecimal memory;

    @ApiModelProperty("总磁盘信息")
    private BigDecimal disk;

    @ApiModelProperty("已使用cpu信息")
    private BigDecimal usedCpu;

    @ApiModelProperty("已使用内存信息")
    private BigDecimal usedMemory;

    @ApiModelProperty("已使用磁盘信息")
    private BigDecimal usedDisk;
}
