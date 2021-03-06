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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 集群配置信息
 *
 * @author 技术平台
 * @date 2019-07-16
 */
@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class ClusterConfigDto {

    @ApiModelProperty("DC/OS地址")
    private String dcosEndpoint;

    @ApiModelProperty("镜像库地址")
    private String dockerHubUrl;

    @ApiModelProperty("镜像库凭据ID")
    private Long dockerHubCredentialId;

    @ApiModelProperty("mlb地址列表")
    private List<String> mlbUrls = new ArrayList<>();
}
