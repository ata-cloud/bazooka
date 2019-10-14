package net.atayun.bazooka.pms.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/10 上午10:08
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class DeployConfigPortDistribution {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    /**
     * 配置ID
     */
    @ManyToOne
    @JoinColumn(name = "config_id")
    private AppEnvDeployConfig appEnvDeployConfig;

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
//    @Column(name = "port_state")
//    private ServicePortState portState;
}
