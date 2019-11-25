package net.atayun.bazooka.deploy.biz.v2.service.app.platform;

import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;

/**
 * @author Ping
 */
public interface Platform {

    /**
     * 启动服务
     *
     * @param appOpt         appOpt
     * @param appOptFlowStep appOptFlowStep
     */
    void startApp(AppOpt appOpt, AppOptFlowStep appOptFlowStep);
}
