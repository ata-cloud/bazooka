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

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月29日 17:00:00
 * @work 集群Marathon配置详情 Dto
 */
@Setter
@Getter
@ApiModel
public class ClusterMarathonConfigRspDto implements Serializable {

    private static final long serialVersionUID = 2607057832488177146L;

    @ApiModelProperty("集群id")
    private Long clusterId;

    @ApiModelProperty("集群类型")
    private String clusterType;

    @ApiModelProperty("是否开启监听(0:不开启 1:开启)")
    private Boolean enableMonitor;

    @ApiModelProperty("有效的marathon地址")
    private String url;
}
