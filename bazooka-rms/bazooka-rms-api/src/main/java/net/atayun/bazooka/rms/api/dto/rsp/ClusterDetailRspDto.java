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

import net.atayun.bazooka.rms.api.dto.EnvResourceDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 集群详情响应 Dto
 */
@Setter
@Getter
@ApiModel
public class ClusterDetailRspDto implements Serializable {

    private static final long serialVersionUID = -4921326749846366427L;

    @ApiModelProperty("集群id")
    private Long clusterId;

    @ApiModelProperty("集群名称")
    private String name;

    @ApiModelProperty("机房类型")
    private String roomType;

    @ApiModelProperty("集群状态: 0:正常(绿色) 1:可用(黄色) 2:异常(红色)")
    private String status;

    @ApiModelProperty("集群类型 0:DC/OS")
    private String type;

    @ApiModelProperty("集群版本信息")
    private String version;

    @ApiModelProperty("总cpu信息")
    private BigDecimal cpu;

    @ApiModelProperty("总内存信息")
    private BigDecimal memory;

    @ApiModelProperty("总磁盘信息")
    private BigDecimal disk;

    @ApiModelProperty("集群master")
    private List<ClusterConfigDetailRspDto> marathons = new ArrayList<>();

    @ApiModelProperty("集群mlb")
    private List<ClusterConfigDetailRspDto> mlbs = new ArrayList<>();

    @ApiModelProperty("镜像库")
    private List<ClusterConfigDetailRspDto> dockerHubs = new ArrayList<>();

    @ApiModelProperty("集群环境对应资源信息")
    private List<EnvResourceDto> envResources = new ArrayList<>();
}
