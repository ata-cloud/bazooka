package net.atayun.bazooka.pms.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/9 下午5:16
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class ClusterConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cluster_id")
    private Cluster cluster;


    /**
     * 集群类型: 0:集群master 1:集群mlb 2:镜像库 3:Jenkins 4:GitLab 5:AtaCloud
     */
    @Column(name = "type")
    private String type;

    /**
     * url地址
     */
    @Column(name = "url")
    private String url;

    /**
     * 状态: 0:正常(绿色) 1:可用(黄色) 2:异常(红色)
     */
    @Column(name = "status")
    private String status;

    /**
     * 版本
     */
    @Column(name = "version")
    private String version;

    /**
     * 获取类型名称
     *
     * @param type
     * @return
     */
    public static String getTypeName(String type) {
        switch (type) {
            case "0":
                return "DC/OS";
            case "1":
                return "MLB";
            case "2":
                return "镜像库";
            case "3":
                return "Jenkins";
            case "4":
                return "GitLab";
            case "5":
                return "AtaCloud";
            default:
                return type;
        }
    }
}
