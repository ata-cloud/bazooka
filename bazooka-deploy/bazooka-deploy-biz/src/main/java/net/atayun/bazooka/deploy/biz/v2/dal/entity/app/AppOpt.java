package net.atayun.bazooka.deploy.biz.v2.dal.entity.app;

import com.youyu.common.entity.JdbcMysqlEntity;
import lombok.Getter;
import lombok.Setter;
import net.atayun.bazooka.deploy.biz.v2.enums.AppOptEnum;
import net.atayun.bazooka.deploy.biz.v2.enums.AppOptStatusEnum;

import javax.persistence.Table;
import java.util.Map;

/**
 * @author Ping
 */
@Getter
@Setter
@Table(name = "deploy_app_opt")
public class AppOpt extends JdbcMysqlEntity<Long> {

    private Long appId;

    private String appName;

    private Long projectId;

    private Long envId;

    private AppOptEnum opt;

    private Map<String, Object> detail;

    private AppOptStatusEnum status;

    private String remark;

    private Long deployConfigId;

    private String branch;

    private String dockerImageTag;

    private String appDeployUuid;

    private String appDeployVersion;

    private String appRunServiceId;

    private String appDeployConfig;
}
