package net.atayun.bazooka.deploy.biz.v2.service.app.opt.remark;

import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.constant.CommonConstants;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.rms.api.api.EnvApi;
import org.springframework.stereotype.Component;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = AppOptType.class, number = "PUSH_IMAGE")
public class AppOptType4PushImage implements AppOptType {

    @Override
    public String remark(AppOpt appOpt) {
        return "目标镜像库: " + getTargetDockerRegistry(appOpt) + ", Tag: " + appOpt.getDockerImageTag();
    }

    public static String getTargetDockerRegistry(AppOpt appOpt) {
        String targetDockerRegistry;
        if (appOpt.isExternalDockerRegistry()) {
            targetDockerRegistry = appOpt.getTargetDockerRegistry();
        } else {
            targetDockerRegistry = getBean(EnvApi.class).getEnvConfiguration(appOpt.getTargetEnvId())
                    .ifNotSuccessThrowException()
                    .getData()
                    .getDockerHubUrl();
        }
        if (targetDockerRegistry.startsWith(CommonConstants.PROTOCOL)) {
            targetDockerRegistry = targetDockerRegistry.replace(CommonConstants.PROTOCOL, "");
        }
        if (targetDockerRegistry.startsWith(CommonConstants.SECURE_PROTOCOL)) {
            targetDockerRegistry = targetDockerRegistry.replace(CommonConstants.SECURE_PROTOCOL, "");
        }
        return targetDockerRegistry;
    }
}