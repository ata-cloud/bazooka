package net.atayun.bazooka.deploy.biz.v2.service.marathon;

import net.atayun.bazooka.deploy.api.param.MarathonCallbackParam;
import net.atayun.bazooka.deploy.api.param.MarathonTaskFailureCallbackParam;

/**
 * @author Ping
 */
public interface MarathonService {

    void marathonCallback(MarathonCallbackParam marathonCallbackParam);

    void marathonTaskFailureCallback(MarathonTaskFailureCallbackParam marathonTaskFailureCallbackParam);
}
