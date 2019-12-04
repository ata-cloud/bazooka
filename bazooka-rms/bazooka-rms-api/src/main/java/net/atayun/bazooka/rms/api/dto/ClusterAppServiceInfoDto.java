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
import lombok.Getter;
import lombok.Setter;
import net.atayun.bazooka.rms.api.enums.ClusterAppServiceStatusEnum;

import java.io.Serializable;
import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 集群应用服务信息Dto
 */
@Setter
@Getter
@ApiModel("集群应用服务信息Dto")
public class ClusterAppServiceInfoDto implements Serializable {

    private static final long serialVersionUID = 5736902715073648462L;

    /**
     * 状态
     *
     * @see ClusterAppServiceStatusEnum
     */
    @ApiModelProperty("状态 0:未发布 1:已关闭 2:启动中 3:运行中")
    private String status;

    @ApiModelProperty("cpu")
    private BigDecimal cpu;

    @ApiModelProperty("内存")
    private BigDecimal memory;

    @ApiModelProperty("实例数")
    private Integer instances;

    public ClusterAppServiceInfoDto() {
    }

    public ClusterAppServiceInfoDto(String status) {
        this.setStatus(status);
        this.setCpu(ZERO);
        this.setMemory(ZERO);
        this.setInstances(0);
    }
}
