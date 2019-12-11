package net.atayun.bazooka.deploy.biz.v2.service.app.step;

import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;

/**
 * @author Ping
 */
public interface Cancel {

    void cancel(AppOpt appOpt, AppOptFlowStep appOptFlowStep);
}
