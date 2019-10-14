package net.atayun.bazooka.pms.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/9 下午5:14
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Cluster {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
//
//    private List<ClusterNode> clusterNodes;
//
//    private List<Env> envs;
//
//    private List<ClusterConfig> clusterConfigs;
//
//    private List<ClusterCommandConfig> clusterCommandConfigs;

        /**
     * 集群名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 机房类型
     */
    @Column(name = "room_type")
    private String roomType;

    /**
     * 集群状态: 0:正常(绿色) 1:可用(黄色) 2:异常(红色)
     */
    @Column(name = "status")
    private String status;

    /**
     * 集群类型 0:DC/OS
     */
    @Column(name = "type")
    private String type;

    /**
     * 集群版本信息
     */
    @Column(name = "version")
    private String version;

    /**
     * cpu信息
     */
    @Column(name = "cpu")
    private BigDecimal cpu;

    /**
     * 内存信息
     */
    @Column(name = "memory")
    private BigDecimal memory;

    /**
     * 磁盘信息
     */
    @Column(name = "disk")
    private BigDecimal disk;

    /**
     * 是否开启监听
     */
    @Column(name = "enable_monitor")
    private Boolean enableMonitor;
}
