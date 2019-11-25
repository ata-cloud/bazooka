package net.atayun.bazooka.deploy.biz.v2.service.marathon;

import net.atayun.bazooka.deploy.biz.v2.param.MarathonCallbackParam;

/**
 * @author Ping
 */
public interface MarathonService {
    void marathonCallback(MarathonCallbackParam marathonCallbackParam);
}
