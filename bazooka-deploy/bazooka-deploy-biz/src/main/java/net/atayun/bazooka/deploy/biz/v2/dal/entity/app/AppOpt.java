package net.atayun.bazooka.deploy.biz.v2.dal.entity.app;

import com.alibaba.fastjson.JSONObject;
import com.youyu.common.entity.JdbcMysqlEntity;
import lombok.Getter;
import lombok.Setter;
import net.atayun.bazooka.base.bean.StrategyNumBean;
import net.atayun.bazooka.base.enums.AppOptEnum;
import net.atayun.bazooka.deploy.biz.v2.enums.AppOptStatusEnum;
import net.atayun.bazooka.deploy.biz.v2.param.AppActionParam;
import net.atayun.bazooka.deploy.biz.v2.service.app.opt.remark.AppOptRemark;
import net.atayun.bazooka.deploy.biz.v2.service.app.opt.remark.AppOptType;
import net.atayun.bazooka.pms.api.dto.AppInfoDto;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.HashMap;
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

    @Column(name = "opt")
    private AppOptEnum opt;

    @Column(name = "detail")
    private Map<String, Object> detail;

    @Column(name = "status")
    private AppOptStatusEnum status;

    private String remark;

    private String appDeployUuid;

    private String appDeployVersion;

    private String appRunServiceId;

    private String appDeployConfig;

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
        this.appDeployUuid = template.getAppDeployUuid();
        this.appDeployVersion = template.getAppDeployVersion();
        this.appDeployConfig = template.getAppDeployConfig();
        this.appRunServiceId = template.getAppRunServiceId();
        this.remark = StrategyNumBean.getBeanInstance(AppOptRemark.class, this.opt.name()).remark(this);
        this.appDeployUuid = "";
        this.appDeployVersion = "";
        this.appRunServiceId = "";
        this.appDeployConfig = "";
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
        return Long.parseLong(detail.get("deployConfigId").toString());
    }

    public String getBranch() {
        return (String) detail.get("branch");
    }

    public String getDockerImageTag() {
        return (String) detail.get("dockerImageTag");
    }

    public Long getImageId() {
        return Long.parseLong(detail.get("imageId").toString());
    }

    public Integer getInstance() {
        return Integer.parseInt(detail.get("instance").toString());
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
        return Long.parseLong(detail.get("templateEventId").toString());
    }

    public Long getCredentialId() {
        return Long.parseLong(detail.get("credentialId").toString());
    }

    public String getUsername() {
        return (String) detail.get("username");
    }

    public String getPassword() {
        return (String) detail.get("password");
    }

    public Long getTargetEnvId() {
        return Long.parseLong(detail.get("targetEnvId").toString());
    }

    public Boolean needAuth() {
        return (Boolean) detail.get("needAuth");
    }

    //====================
}
