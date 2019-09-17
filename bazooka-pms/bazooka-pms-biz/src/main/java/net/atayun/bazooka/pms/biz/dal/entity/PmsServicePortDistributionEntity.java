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

import javax.persistence.*;

import net.atayun.bazooka.pms.api.enums.ServicePortState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pms_service_port_distribution")
public class PmsServicePortDistributionEntity extends com.youyu.common.entity.JdbcMysqlEntity<Long> {
    public PmsServicePortDistributionEntity(Long projectId, Long appId, Long envId, Integer containerPort, Integer servicePort, Boolean continuous, ServicePortState portState) {
        this.projectId = projectId;
        this.appId = appId;
        this.envId = envId;
        this.containerPort = containerPort;
        this.servicePort = servicePort;
        this.continuous = continuous;
        this.portState = portState;
    }

    public PmsServicePortDistributionEntity(Long projectId, Long appId, Long envId, Integer containerPort) {
        this.projectId = projectId;
        this.appId = appId;
        this.envId = envId;
        this.containerPort = containerPort;
    }

    public PmsServicePortDistributionEntity(Long projectId, Long appId, Long envId) {
        this.projectId = projectId;
        this.appId = appId;
        this.envId = envId;
    }

    public PmsServicePortDistributionEntity(Long projectId, Long envId, Integer servicePort) {
        this.projectId = projectId;
        this.envId = envId;
        this.servicePort = servicePort;
    }

    public PmsServicePortDistributionEntity(Long projectId, Long envId) {
        this.projectId = projectId;
        this.envId = envId;
    }

    /**
     * 项目ID
     */
    @Column(name = "project_id")
    private Long projectId;

    /**
     * 应用ID
     */
    @Column(name = "app_id")
    private Long appId;

    /**
     * 环境ID
     */
    @Column(name = "env_id")
    private Long envId;

    /**
     * 配置ID
     */
    @Column(name = "config_id")
    private Long configId;

    /**
     * 容器端口
     */
    @Column(name = "container_port")
    private Integer containerPort;

    /**
     * 服务端口
     */
    @Column(name = "service_port")
    private Integer servicePort;

    /**
     * 是否连续
     */
    @Column(name = "continuous")
    private Boolean continuous;

    /**
     * 端口状态
     */
    @Column(name = "port_state")
    private ServicePortState portState;
}