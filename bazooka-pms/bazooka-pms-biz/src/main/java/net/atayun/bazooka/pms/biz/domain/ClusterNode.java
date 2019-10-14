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
public class ClusterNode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cluster_id")
    private Cluster cluster;


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

}
