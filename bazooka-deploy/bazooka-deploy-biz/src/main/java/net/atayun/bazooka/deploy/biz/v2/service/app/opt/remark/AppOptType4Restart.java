package net.atayun.bazooka.deploy.biz.v2.service.app.opt.remark;

import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import org.springframework.stereotype.Component;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = AppOptType.class, number = "RESTART")
public class AppOptType4Restart implements AppOptType {

    @Override
    public String remark(AppOpt appOpt) {
        return "重启";
    }
}
