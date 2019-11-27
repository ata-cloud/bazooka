package net.atayun.bazooka.deploy.biz.v2.service.app.opt.remark;

import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.feign.AppApi;
import org.springframework.stereotype.Component;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = AppOptType.class, number = "MARATHON_BUILD_DEPLOY")
public class AppOptType4MarathonBuildDeploy implements AppOptType {

    @Override
    public String remark(AppOpt appOpt) {
        AppDeployConfigDto appDeployConfig = getBean(AppApi.class).getAppDeployConfigInfoById(appOpt.getDeployConfigId())
                .ifNotSuccessThrowException()
                .getData();
        return "发布配置: " + appDeployConfig.getConfigName();
    }
}