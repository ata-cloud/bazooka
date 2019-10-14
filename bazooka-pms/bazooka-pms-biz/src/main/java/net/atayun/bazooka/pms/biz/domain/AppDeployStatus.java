package net.atayun.bazooka.pms.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/9 下午5:18
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class AppDeployStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "env_id")
    private Env env;
    
    @ManyToOne
    @JoinColumn(name = "app_id")
    private App app;

        /**
     * 部署中
     */
    @Column(name = "deploying")
    private Boolean deploying;

    /**
     * 部署类型
     */
//    @Column(name = "deploy_type")
//    private AppOperationEnum deployType;
}
