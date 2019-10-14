package net.atayun.bazooka.pms.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/9 下午5:26
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class AppEnvDeployConfig {

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
     * 配置名
     */
    @Column(name = "config_name")
    private String configName;

    /**
     * 描述
     */
    @Column(name = "config_description")
    private String configDescription;

//    /**
//     * 部署类型
//     */
//    @Column(name = "deploy_mode")
//    private DeployModeEnum deployMode;

    /**
     * git 分支白名单
     */
    @Column(name = "git_branch_allow")
    private String gitBranchAllow;

    /**
     * git 分支黑名单
     */
    @Column(name = "git_branch_deny")
    private String gitBranchDeny;

    /**
     * 编译命令
     */
    @Column(name = "compile_command")
    private String compileCommand;

    /**
     * Dockerfile 路径
     */
    @Column(name = "dockerfile_path")
    private String dockerfilePath;

    /**
     * cpus(核)
     */
    @Column(name = "cpus")
    private Double cpus;

    /**
     * 内存(MB)
     */
    @Column(name = "memory")
    private Integer memory;

    /**
     * 磁盘大小(GB)
     */
    @Column(name = "disk")
    private Integer disk;

    /**
     * 实例个数
     */
    @Column(name = "instance")
    private Integer instance;

    /**
     * 启动命令
     */
    @Column(name = "start_command")
    private String startCommand;

    /**
     * 端口映射
     */
    @Column(name = "port_mappings")
    private String portMappings;

    /**
     * 环境变量
     */
    @Column(name = "environment_variable")
    private String environmentVariable;

    /**
     * 挂载
     */
    @Column(name = "volumes")
    private String volumes;

    /**
     * 健康检查
     */
    @Column(name = "health_checks")
    private String healthChecks;


    /**
     * 是否已删除
     */
    @Column(name = "is_deleted")
    private Boolean isDeleted;
}
