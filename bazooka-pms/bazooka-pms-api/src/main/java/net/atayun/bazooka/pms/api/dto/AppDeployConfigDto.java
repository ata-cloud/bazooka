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

import com.youyu.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.atayun.bazooka.base.enums.deploy.DeployModeEnum;
import net.atayun.bazooka.pms.api.param.HealthCheck;
import net.atayun.bazooka.pms.api.param.PortMapping;
import net.atayun.bazooka.pms.api.param.VolumeMount;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2019-07-16
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class AppDeployConfigDto extends BaseDto<Long> {
    public AppDeployConfigDto(Long id, Long appId, Long envId, String envName, String configName, String configDescription, DeployModeEnum deployMode, String gitBranchAllow, String gitBranchDeny, String compileCommand, String dockerfilePath, Double cpus, Integer memory, Integer disk, Integer instance, String startCommand, List<Long> clusterNodes, List<PortMapping> portMappings, Map<String, Object> environmentVariable, List<VolumeMount> volumes, List<HealthCheck> healthChecks, String updateAuthor, LocalDateTime updateTime) {
        super(id);
        this.appId = appId;
        this.envId = envId;
        this.envName = envName;
        this.configName = configName;
        this.configDescription = configDescription;
        this.deployMode = deployMode;
        this.gitBranchAllow = gitBranchAllow;
        this.gitBranchDeny = gitBranchDeny;
        this.compileCommand = compileCommand;
        this.dockerfilePath = dockerfilePath;
        this.cpus = cpus;
        this.memory = memory;
        this.disk = disk;
        this.instance = instance;
        this.startCommand = startCommand;
        this.clusterNodes = clusterNodes;
        this.portMappings = portMappings;
        this.environmentVariable = environmentVariable;
        this.volumes = volumes;
        this.healthChecks = healthChecks;
        this.updateAuthor = updateAuthor;
        this.updateTime = updateTime;
    }

    @NotNull(message = "所属服务id不能空")
    @ApiModelProperty(value = "所属服务id")
    private Long appId;

    @NotNull(message = "环境id不能空")
    @ApiModelProperty(value = "环境id")
    private Long envId;

    @Size(max = 30, message = "配置名长度不能超过30")
    @NotBlank(message = "配置名不能空")
    @ApiModelProperty(value = "配置名")
    private String configName;

    @Size(max = 100, message = "描述长度不能超过100")
    @ApiModelProperty(value = "描述")
    private String configDescription;

    @NotNull(message = "所属服务id不能空")
    @ApiModelProperty(value = "发布方式")
    private DeployModeEnum deployMode;

    @ApiModelProperty(value = "git 分支白名单")
    private String gitBranchAllow;

    @ApiModelProperty(value = "git 分支黑名单")
    private String gitBranchDeny;

    @Size(max = 1000, message = "编译命令长度不能超过1000")
    @ApiModelProperty(value = "编译命令")
    private String compileCommand;

    @Size(max = 256, message = "Dockerfile 路径长度不能超过256")
    @ApiModelProperty(value = "Dockerfile 路径")
    private String dockerfilePath;

    @NotNull(message = "cpus不能空")
    @ApiModelProperty(value = "cpus(核)")
    private Double cpus;

    @NotNull(message = "内存不能空")
    @ApiModelProperty(value = "内存(MB)")
    private Integer memory;

    @ApiModelProperty(value = "磁盘大小(GB)")
    private Integer disk;

    @NotNull(message = "实例个数不能空")
    @ApiModelProperty(value = "实例个数")
    private Integer instance;

    @Size(max = 1000, message = "启动命令长度不能超过1000")
    @ApiModelProperty(value = "启动命令")
    private String startCommand;

    @NotNull(message = "端口映射不能空")
    @ApiModelProperty(value = "端口映射")
    private List<PortMapping> portMappings;

    @ApiModelProperty(value = "环境变量")
    private Map<String, Object> environmentVariable;

    @ApiModelProperty(value = "挂载")
    private List<VolumeMount> volumes;

    @Valid
    @ApiModelProperty(value = "健康检查")
    private List<HealthCheck> healthChecks;

    /**
     * 单节点时，发布节点列表
     */
    @ApiModelProperty(value = "发布节点列表")
    private List<Long> clusterNodes;


    private String envName;
    private String updateAuthor;
    private LocalDateTime updateTime;

}

