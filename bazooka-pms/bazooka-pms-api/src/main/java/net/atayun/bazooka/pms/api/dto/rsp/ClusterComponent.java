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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 集群组件
 *
 * @author WangSongJun
 * @date 2019-08-26
 */
@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class ClusterComponent {
    @ApiModelProperty("组件名称")
    private String name;
    @ApiModelProperty("组件版本")
    private String version;
    @ApiModelProperty("组件状态: 0:正常(绿色) 1:可用(黄色) 2:异常(红色)")
    private String status;
    @ApiModelProperty("组件地址")
    private String url;
}
