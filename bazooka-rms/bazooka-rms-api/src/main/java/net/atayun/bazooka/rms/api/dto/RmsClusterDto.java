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

import com.youyu.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 资源集群 Dto
 */
@Setter
@Getter
@ApiModel
public class RmsClusterDto extends BaseDto<Long> {

    private static final long serialVersionUID = 1490518272784421503L;

    @ApiModelProperty("集群名称")
    private String name;

    @ApiModelProperty("集群状态: 0:正常(绿色) 1:可用(黄色) 2:异常(红色)")
    private String status;

    @ApiModelProperty("cpu")
    private BigDecimal cpu;

    @ApiModelProperty("内存")
    private BigDecimal memory;

    @ApiModelProperty("磁盘")
    private BigDecimal disk;

    @ApiModelProperty("集群类型")
    private String type;

}

