package net.atayun.bazooka.deploy.biz.v2.dal.entity.app;

import com.alibaba.fastjson.JSONObject;
import com.youyu.common.entity.JdbcMysqlEntity;
import lombok.Getter;
import lombok.Setter;
import net.atayun.bazooka.base.bean.StrategyNumBean;
import net.atayun.bazooka.base.enums.AppOptEnum;
import net.atayun.bazooka.deploy.biz.v2.enums.AppOptStatusEnum;
import net.atayun.bazooka.deploy.biz.v2.param.AppActionParam;
import net.atayun.bazooka.deploy.biz.v2.service.app.opt.remark.AppOptType;
import net.atayun.bazooka.pms.api.dto.AppInfoDto;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ping
 */
@Table(name = "deploy_app_opt")
public class AppOpt extends JdbcMysqlEntity<Long> {
    @Getter
    @Setter
    private Long appId;

    @Getter
    @Setter
    private String appName;

    @Getter
    @Setter
    private Long projectId;

    @Getter
    @Setter
    private Long envId;

    @Getter
    @Setter
    @Column(name = "opt")
    private AppOptEnum opt;

    @Getter
    @Setter
    @Column(name = "detail")
    private Map<String, Object> detail;

    @Getter
    @Setter
    @Column(name = "status")
    private AppOptStatusEnum status;

    @Getter
    @Setter
    private String remark;

    @Getter
    @Setter
    private String appDeployUuid;

    @Getter
    @Setter
    private String appDeployVersion;

    @Getter
    @Setter
    private String appRunServiceId;

    @Getter
    @Setter
    private String appDeployConfig;

    @Setter
    private String dockerImageTag;

    public AppOpt() {
    }

    public AppOpt(AppActionParam appActionParam, AppInfoDto appInfo) {
        this.appId = appActionParam.getAppId();
        this.appName = appInfo.getAppName();
        this.projectId = appInfo.getProjectId();
        this.envId = appActionParam.getEnvId();
        this.opt = appActionParam.getEvent();
        JSONObject jsonObject = JSONObject.parseObject(appActionParam.getDetail());
        this.detail = new HashMap<>(jsonObject);
        process();
        this.remark = StrategyNumBean.getBeanInstance(AppOptType.class, this.opt.name()).remark(this);
        this.appDeployUuid = "";
        this.appDeployVersion = "";
        this.appRunServiceId = "";
        this.appDeployConfig = "";
        this.dockerImageTag = (String) this.detail.get("dockerImageTag");
    }

    public AppOpt(AppActionParam appActionParam, AppInfoDto appInfo, AppOpt template) {
        this.appId = appActionParam.getAppId();
        this.appName = appInfo.getAppName();
        this.projectId = appInfo.getProjectId();
        this.envId = appActionParam.getEnvId();
        this.opt = appActionParam.getEvent();
        JSONObject jsonObject = JSONObject.parseObject(appActionParam.getDetail());
        this.detail = new HashMap<>(jsonObject);
        this.detail.putAll(template.getDetail());
        process();
        this.remark = StrategyNumBean.getBeanInstance(AppOptType.class, this.opt.name()).remark(template);
        this.appDeployUuid = template.getAppDeployUuid();
        this.appDeployVersion = template.getAppDeployVersion();
        this.appDeployConfig = template.getAppDeployConfig();
        this.appRunServiceId = template.getAppRunServiceId();
        this.dockerImageTag = template.getDockerImageTag();
    }

    public void process() {
        this.status = AppOptStatusEnum.PROCESS;
    }

    public void success() {
        this.status = AppOptStatusEnum.SUCCESS;
    }

    public void failure() {
        this.status = AppOptStatusEnum.FAILURE;
    }

    public boolean isProcess() {
        return this.status == AppOptStatusEnum.PROCESS;
    }

    public boolean isSuccess() {
        return this.status == AppOptStatusEnum.SUCCESS;
    }

    public boolean isFailure() {
        return this.status == AppOptStatusEnum.FAILURE;
    }

    public boolean isFinish() {
        return isSuccess() || isFailure();
    }

    //下列都是detail中的值

    public Long getDeployConfigId() {
        Object deployConfigId = detail.get("deployConfigId");
        if (deployConfigId == null) {
            return null;
        }
        return Long.parseLong(deployConfigId.toString());
    }

    public String getBranch() {
        return (String) detail.get("branch");
    }

    public String getDockerImageTag() {
        String dockerImageTag = (String) detail.get("dockerImageTag");
        if (StringUtils.isEmpty(dockerImageTag)) {
            dockerImageTag = this.dockerImageTag;
        }
        return dockerImageTag;
    }

    public Long getImageId() {
        Object imageId = detail.get("imageId");
        if (imageId == null) {
            return null;
        }
        return Long.parseLong(imageId.toString());
    }

    public Integer getInstance() {
        Object instance = detail.get("instance");
        if (instance == null) {
            return null;
        }
        return Integer.parseInt(instance.toString());
    }

    public Double getCpu() {
        return (Double) detail.get("cpu");
    }

    public Double getMemory() {
        return (Double) detail.get("memory");
    }

    public Double getDisk() {
        return (Double) detail.get("disk");
    }

    public String getTargetDockerRegistry() {
        return (String) detail.get("targetDockerRegistry");
    }

    public Boolean isExternalDockerRegistry() {
        return (Boolean) detail.get("isExternalDockerRegistry");
    }

    public Long getTemplateEventId() {
        Object templateEventId = detail.get("templateEventId");
        if (templateEventId == null) {
            return null;
        }
        return Long.parseLong(templateEventId.toString());
    }

    public Long getCredentialId() {
        Object credentialId = detail.get("credentialId");
        if (credentialId == null) {
            return null;
        }
        return Long.parseLong(credentialId.toString());
    }

    public String getUsername() {
        return (String) detail.get("username");
    }

    public String getPassword() {
        return (String) detail.get("password");
    }

    public Long getTargetEnvId() {
        Object targetEnvId = detail.get("targetEnvId");
        if (targetEnvId == null) {
            return null;
        }
        return Long.parseLong(targetEnvId.toString());
    }

    public Boolean needAuth() {
        return (Boolean) detail.get("needAuth");
    }

    //====================
}
