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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月18日 17:00:00
 * @work dcos 节点健康详细信息
 */
@Setter
@Getter
public class NodeHealthInfoDto implements Serializable {

    private static final long serialVersionUID = -1297497510909217067L;

    @JsonProperty(value = "host_ip")
    @ApiModelProperty("主机地址")
    private String hostIp;

    @ApiModelProperty("健康状态(0:健康 非0:不健康)")
    private String health;

    @ApiModelProperty("角色(agent agent_public)")
    private String role;
}
