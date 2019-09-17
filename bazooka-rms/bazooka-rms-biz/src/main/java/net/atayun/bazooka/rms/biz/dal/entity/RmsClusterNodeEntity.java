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

import net.atayun.bazooka.base.dcos.dto.NodeHealthInfoDto;
import net.atayun.bazooka.base.dcos.dto.NodeSlaveDto;
import com.youyu.common.entity.JdbcMysqlEntity;
import lombok.Getter;
import lombok.Setter;
import net.atayun.bazooka.rms.biz.enums.ClusterNodeRoleEnum;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;

import static java.util.Objects.isNull;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 资源集群节点 实体
 */
@Getter
@Setter
@Table(name = "rms_cluster_node")
public class RmsClusterNodeEntity extends JdbcMysqlEntity<Long> {

    /**
     * 集群外键
     */
    @Column(name = "cluster_id")
    private Long clusterId;

    /**
     * 节点id
     */
    @Column(name = "node_id")
    private String nodeId;

    /**
     * ip地址
     */
    @Column(name = "ip")
    private String ip;

    /**
     * 节点类型
     */
    @Column(name = "node_type")
    private String nodeType;

    /**
     * 健康状态: 0:健康(绿色) 1:异常(红色)
     */
    @Column(name = "status")
    private String status;

    /**
     * 容器数量
     */
    @Column(name = "container_quantity")
    private Integer containerQuantity;

    /**
     * 总cpu信息
     */
    @Column(name = "cpu")
    private BigDecimal cpu;

    /**
     * 总内存信息
     */
    @Column(name = "memory")
    private BigDecimal memory;

    /**
     * 总磁盘信息
     */
    @Column(name = "disk")
    private BigDecimal disk;

    /**
     * 已使用cpu信息
     */
    @Column(name = "used_cpu")
    private BigDecimal usedCpu;

    /**
     * 已使用内存信息
     */
    @Column(name = "used_memory")
    private BigDecimal usedMemory;

    /**
     * 已使用磁盘信息
     */
    @Column(name = "used_disk")
    private BigDecimal usedDisk;

    public RmsClusterNodeEntity() {
    }

    public RmsClusterNodeEntity(NodeSlaveDto nodeSlave, NodeHealthInfoDto nodeHealthInfo, RmsClusterConfigEntity rmsClusterConfigEntity) {
        this(nodeSlave, rmsClusterConfigEntity);
        this.status = nodeHealthInfo.getHealth();

    }

    public RmsClusterNodeEntity(NodeSlaveDto nodeSlave, RmsClusterConfigEntity rmsClusterConfigEntity) {
        this.clusterId = rmsClusterConfigEntity.getClusterId();
        this.nodeId = nodeSlave.getId();
        this.ip = nodeSlave.getHostname();
        this.nodeType = isNull(nodeSlave.getAttributes().getPublicIp()) || !nodeSlave.getAttributes().getPublicIp() ? ClusterNodeRoleEnum.AGENT.getDesc() : ClusterNodeRoleEnum.AGENT_PUBLIC.getDesc();
        this.containerQuantity = nodeSlave.getTaskRunning();
        this.cpu = nodeSlave.getResources().getCpus();
        this.memory = nodeSlave.getResources().getMem();
        this.disk = nodeSlave.getResources().getDisk();
        this.usedCpu = nodeSlave.getUsedResources().getCpus();
        this.usedMemory = nodeSlave.getUsedResources().getMem();
        this.usedDisk = nodeSlave.getUsedResources().getDisk();
        this.status = "-1";
    }
}