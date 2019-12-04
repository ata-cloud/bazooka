package net.atayun.bazooka.deploy.biz.v2.service.app.opt.remark;

import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import org.springframework.stereotype.Component;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = AppOptType.class, number = "DELETE_IMAGE")
public class AppOptType4DeleteImage implements AppOptType {

    @Override
    public String remark(AppOpt appOpt) {
        return "镜像Tag: " + appOpt.getFinalDockerImageTag();
    }
}
