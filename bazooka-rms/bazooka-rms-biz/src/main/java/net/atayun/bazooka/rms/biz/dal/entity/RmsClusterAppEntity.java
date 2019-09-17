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
package net.atayun.bazooka.rms.biz.dal.entity;

import net.atayun.bazooka.base.dcos.dto.AppInfoDto;
import net.atayun.bazooka.base.dcos.dto.AppPortMappingDto;
import com.youyu.common.entity.JdbcMysqlEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.join;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 资源集群App 实体
 */
@Getter
@Setter
@Table(name = "rms_cluster_app")
public class RmsClusterAppEntity extends JdbcMysqlEntity<Long> {
    /**
     * 集群外键
     */
    @Column(name = "cluster_id")
    private Long clusterId;

    /**
     * app id
     */
    @Column(name = "app_id")
    private String appId;

    /**
     * 镜像
     */
    @Column(name = "image")
    private String image;

    /**
     * cpu信息
     */
    @Column(name = "cpu")
    private BigDecimal cpu;

    /**
     * 磁盘信息
     */
    @Column(name = "disk")
    private BigDecimal disk;

    /**
     * 内存信息
     */
    @Column(name = "memory")
    private BigDecimal memory;

    /**
     * 实例数
     */
    @Column(name = "instances")
    private Integer instances;

    /**
     * 版本
     */
    @Column(name = "version")
    private String version;

    /**
     * staged的数量
     */
    @Column(name = "tasks_staged")
    private Integer tasksStaged;

    /**
     * 运行的数量
     */
    @Column(name = "tasks_running")
    private Integer tasksRunning;

    /**
     * 通过健康检查数量
     */
    @Column(name = "tasks_healthy")
    private Integer tasksHealthy;

    /**
     * 未通过健康检查数量
     */
    @Column(name = "tasks_unhealthy")
    private Integer tasksUnhealthy;

    /**
     * 当前激活版本
     */
    @Column(name = "active")
    private Boolean active;

    /**
     * app json信息
     */
    @Column(name = "app_json")
    private String appJson;

    /**
     * 容器端口
     */
    @Column(name = "container_port")
    private String containerPort;

    /**
     * 服务端口
     */
    @Column(name = "service_port")
    private String servicePort;

    public RmsClusterAppEntity() {

    }

    public RmsClusterAppEntity(RmsClusterConfigEntity rmsClusterConfigEntity, String appJson, AppInfoDto appInfo) {
        this.clusterId = rmsClusterConfigEntity.getClusterId();
        this.appId = appInfo.getId();
        this.image = appInfo.getContainer().getDocker().getImage();
        this.cpu = appInfo.getCpus();
        this.disk = appInfo.getDisk();
        this.memory = appInfo.getMem();
        this.instances = appInfo.getInstances();
        this.version = appInfo.getVersion();
        this.tasksStaged = appInfo.getTasksStaged();
        this.tasksRunning = appInfo.getTasksRunning();
        this.tasksHealthy = appInfo.getTasksHealthy();
        this.tasksUnhealthy = appInfo.getTasksUnhealthy();
        this.appJson = appJson;
        this.active = true;
        List<AppPortMappingDto> portMappings = appInfo.getContainer().getPortMappings();
        if (isEmpty(portMappings)) {
            return;
        }
        List<String> servicePorts = portMappings.stream().map(AppPortMappingDto::getServicePort).collect(toList());
        this.servicePort = join(servicePorts, ",");
        List<String> containerPorts = portMappings.stream().map(AppPortMappingDto::getContainerPort).collect(toList());
        this.containerPort = join(containerPorts, ",");
    }
}