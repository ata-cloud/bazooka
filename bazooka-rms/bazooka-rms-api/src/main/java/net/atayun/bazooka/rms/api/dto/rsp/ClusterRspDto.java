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
package net.atayun.bazooka.rms.api.dto.rsp;

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
 * @work 集群响应 Dto
 */
@Setter
@Getter
@ApiModel
public class ClusterRspDto implements Serializable {

    private static final long serialVersionUID = 2222535216671972365L;

    @ApiModelProperty("集群id")
    private Long clusterId;

    @ApiModelProperty("集群名称")
    private String name;

    @ApiModelProperty("集群类型: 0:MESOS集群 1:KUBERNETES集群 2:单节点集群")
    private String type;

    @ApiModelProperty("集群状态: 0:正常(绿色) 1:可用(黄色) 2:异常(红色)")
    private String status;

    @ApiModelProperty("环境数量")
    private Integer envQuantity;

    @ApiModelProperty("正常节点数量")
    private Integer normalNodeQuantity;

    @ApiModelProperty("节点数量")
    private Integer nodeQuantity;

    @ApiModelProperty("运行中服务数量")
    private Integer runningServiceQuantity;

    @ApiModelProperty("服务数量")
    private Integer serviceQuantity;

    @ApiModelProperty("总cpu")
    private BigDecimal cpu;

    @ApiModelProperty("总内存")
    private BigDecimal memory;

    @ApiModelProperty("总磁盘")
    private BigDecimal disk;

    @ApiModelProperty("环境cpu")
    private BigDecimal envCpu;

    @ApiModelProperty("环境内存")
    private BigDecimal envMemory;

    @ApiModelProperty("环境磁盘")
    private BigDecimal envDisk;
}
