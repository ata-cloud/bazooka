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
package net.atayun.bazooka.pms.api.dto;

import io.swagger.annotations.ApiModel;
import lombok.*;

/**
 * @author rache
 * @date 2019-07-12
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "PmsEnv")
public class EnvDto {
    /**
     * 环境id
     */
    private Long envId;
    /**
     * 环境名称
     */
    private String envName;
    /**
     * 开始端口
     */
    private Integer portStart;
    /**
     * 结束端口
     */
    private Integer portEnd;

    /**
     * 集群id
     */
    private Long clusterId;

    /**
     * 集群类型
     */
    private String clusterType;

}
