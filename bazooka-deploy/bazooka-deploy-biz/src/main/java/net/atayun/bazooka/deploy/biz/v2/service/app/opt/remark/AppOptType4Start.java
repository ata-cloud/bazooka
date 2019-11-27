package net.atayun.bazooka.deploy.biz.v2.service.app.opt.remark;

import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import org.springframework.stereotype.Component;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = AppOptType.class, number = "START")
public class AppOptType4Start implements AppOptType {

    @Override
    public String remark(AppOpt appOpt) {
        return "实例个数: " + 0 + " -> " + appOpt.getInstance();
    }
}
