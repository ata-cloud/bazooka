package net.atayun.bazooka.deploy.biz.v2.service.app;

import net.atayun.bazooka.base.enums.AppOptEnum;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.pms.api.feign.AppApi;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
public class AppStatusOpt {

    /**
     * 更新app的状态
     *
     * @param isOpt isOpt
     */
    public static void updateAppStatus(AppOpt appOpt, boolean isOpt) {
        getBean(AppApi.class).updateAppDeployStatus(appOpt.getAppId(), appOpt.getEnvId(), isOpt, appOpt.getOpt());
    }

    public static void updateAppStatus(Long appId, Long envId, boolean isOpt, AppOptEnum appOptEnum) {
        getBean(AppApi.class).updateAppDeployStatus(appId, envId, isOpt, appOptEnum);
    }
}
