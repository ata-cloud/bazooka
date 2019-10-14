package net.atayun.bazooka.pms.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/9 下午5:49
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class App {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "credentials_id")
    private Credentials credentials;


    /**
     * 应用名称
     */
    @Column(name = "app_name")
    private String appName;

    /**
     * 应用代码
     */
    @Column(name = "app_code")
    private String appCode;

    /**
     * 应用类型
     */
//    @Column(name = "app_kind")
//    private AppKindEnum appKind;

    /**
     * 应用描述
     */
    @Column(name = "description")
    private String description;

    /**
     * 负责人
     */
    @Column(name = "leader_id")
    private Long leaderId;

    /**
     * gitlab id
     */
    @Column(name = "gitlab_id")
    private Integer gitlabId;

    /**
     * gitlab url
     */
    @Column(name = "gitlab_url")
    private String gitlabUrl;

    /**
     * git credential id
     */
    @Column(name = "git_credential_id")
    private Long gitCredentialId;

    /**
     * 对应dcos的服务ID
     */
    @Column(name = "dcos_service_id")
    private String dcosServiceId;

    /**
     * docker 镜像名
     */
    @Column(name = "docker_image_name")
    private String dockerImageName;

    /**
     * 是否删除
     */
    @Column(name = "is_deleted")
    private Boolean isDeleted;
}
