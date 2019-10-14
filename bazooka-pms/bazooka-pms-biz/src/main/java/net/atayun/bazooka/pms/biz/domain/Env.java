package net.atayun.bazooka.pms.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/9 下午5:16
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Env {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cluster_id")
    private Cluster cluster;


    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity = Project.class)
    @JoinTable(name = "env_project_relation", joinColumns = @JoinColumn(name = "env_id"), inverseJoinColumns = @JoinColumn(name = "project_id"))
    private List<Project> projects;


    /**
     * 环境名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 环境代码
     */
    @Column(name = "code")
    private String code;

    /**
     * 环境状态(0:正常使用,1:暂停发布)
     */
    @Column(name = "state")
    private String state;

    /**
     * CPU
     */
    @Column(name = "cpus")
    private BigDecimal cpus;

    /**
     * 内存
     */
    @Column(name = "memory")
    private BigDecimal memory;

    /**
     * 磁盘
     */
    @Column(name = "disk")
    private BigDecimal disk;

}
