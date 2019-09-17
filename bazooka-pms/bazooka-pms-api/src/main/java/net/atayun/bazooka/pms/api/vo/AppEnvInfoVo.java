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
package net.atayun.bazooka.pms.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author WangSongJun
 * @date 2019-07-31
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("环境下应用概览")
public class AppEnvInfoVo {

    @ApiModelProperty("应用ID")
    private Long appId;

    @ApiModelProperty("应用名称")
    private String appName;

    @ApiModelProperty("项目ID")
    private Long projectId;

    @ApiModelProperty("项目名称")
    private String projectName;

    @ApiModelProperty("代码库")
    private String gitRepository;

    @ApiModelProperty("环境ID")
    private Long envId;
    @ApiModelProperty("环境名称")
    private String envName;

    @ApiModelProperty("集群ID")
    private Long clusterId;

    @ApiModelProperty("集群名称")
    private String clusterName;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("cpu")
    private BigDecimal cpu;

    @ApiModelProperty("内存")
    private BigDecimal memory;

    @ApiModelProperty("实例数")
    private Integer instances;
}
