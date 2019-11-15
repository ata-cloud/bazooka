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
package net.atayun.bazooka.pms.biz.dal.entity;

import com.youyu.common.entity.JdbcMysqlEntity;
import com.youyu.common.enums.IsDeleted;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.atayun.bazooka.base.enums.deploy.DeployModeEnum;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 应用发布配置
 *
 * @author WangSongJun
 * @date 2019-07-16
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pms_app_deploy_config")
public class AppDeployConfigEntity extends JdbcMysqlEntity<Long> {
    public AppDeployConfigEntity(Long id, Long appId, Long envId, String configName, String configDescription, DeployModeEnum deployMode, String gitBranchAllow, String gitBranchDeny, String compileCommand, String dockerfilePath, Double cpus, Integer memory, Integer disk, Integer instance, String startCommand, String portMappings, String environmentVariable, String volumes, String healthChecks, IsDeleted isDeleted, String clusterNodes) {
        super(id);
        this.appId = appId;
        this.envId = envId;
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
        this.portMappings = portMappings;
        this.environmentVariable = environmentVariable;
        this.volumes = volumes;
        this.healthChecks = healthChecks;
        this.isDeleted = isDeleted;
        this.clusterNodes = clusterNodes;
    }

    public AppDeployConfigEntity(Long id, IsDeleted isDeleted) {
        super(id);
        this.isDeleted = isDeleted;
    }

    public AppDeployConfigEntity(Long appId, Long envId, IsDeleted isDeleted) {
        this.appId = appId;
        this.envId = envId;
        this.isDeleted = isDeleted;
    }

    public AppDeployConfigEntity(Long appId, Long envId, String configName, IsDeleted isDeleted) {
        this.appId = appId;
        this.envId = envId;
        this.configName = configName;
        this.isDeleted = isDeleted;
    }

    /**
     * 所属服务id
     */
    @Column(name = "app_id")
    private Long appId;

    /**
     * 环境id
     */
    @Column(name = "env_id")
    private Long envId;

    /**
     * 配置名
     */
    @Column(name = "config_name")
    private String configName;

    /**
     * 描述
     */
    @Column(name = "config_description")
    private String configDescription;

    /**
     * 部署类型
     */
    @Column(name = "deploy_mode")
    private DeployModeEnum deployMode;

    /**
     * git 分支白名单
     */
    @Column(name = "git_branch_allow")
    private String gitBranchAllow;

    /**
     * git 分支黑名单
     */
    @Column(name = "git_branch_deny")
    private String gitBranchDeny;

    /**
     * 编译命令
     */
    @Column(name = "compile_command")
    private String compileCommand;

    /**
     * Dockerfile 路径
     */
    @Column(name = "dockerfile_path")
    private String dockerfilePath;

    /**
     * cpus(核)
     */
    @Column(name = "cpus")
    private Double cpus;

    /**
     * 内存(MB)
     */
    @Column(name = "memory")
    private Integer memory;

    /**
     * 磁盘大小(GB)
     */
    @Column(name = "disk")
    private Integer disk;

    /**
     * 实例个数
     */
    @Column(name = "instance")
    private Integer instance;

    /**
     * 启动命令
     */
    @Column(name = "start_command")
    private String startCommand;

    /**
     * 端口映射
     */
    @Column(name = "port_mappings")
    private String portMappings;

    /**
     * 环境变量
     */
    @Column(name = "environment_variable")
    private String environmentVariable;

    /**
     * 挂载
     */
    @Column(name = "volumes")
    private String volumes;

    /**
     * 健康检查
     */
    @Column(name = "health_checks")
    private String healthChecks;


    /**
     * 是否已删除
     */
    @Column(name = "is_deleted")
    private IsDeleted isDeleted;

    /**
     * 发布节点
     */
    @Column(name = "cluster_nodes")
    private String clusterNodes;
}