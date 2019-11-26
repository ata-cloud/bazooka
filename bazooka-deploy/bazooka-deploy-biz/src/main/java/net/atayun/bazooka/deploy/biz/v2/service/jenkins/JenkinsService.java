package net.atayun.bazooka.deploy.biz.v2.service.jenkins;

import net.atayun.bazooka.deploy.biz.v2.param.StepCallbackParam;

/**
 * @author Ping
 */
public interface JenkinsService {

    void stepCallback(StepCallbackParam stepCallbackParam);
}
