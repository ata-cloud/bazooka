package net.atayun.bazooka.deploy.biz.v2.service.app.step.deploymode;

import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;

/**
 * @author Ping
 */
public interface DeployMode {

    void check(AppOpt appOpt, AppOptFlowStep appOptFlowStep);
}
