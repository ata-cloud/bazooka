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
package net.atayun.bazooka.base.dcos.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月18日 17:00:00
 * @work dcos 节点健康信息
 */
@Setter
@Getter
public class NodeHealthDto implements Serializable {

    private static final long serialVersionUID = 7493763102475132478L;

    @ApiModelProperty("节点列表信息")
    private List<NodeHealthInfoDto> nodes = new ArrayList<>();
}
