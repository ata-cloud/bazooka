package net.atayun.bazooka.gateway.component.strategy;

import com.youyu.common.api.Result;

/**
 * @author Ping
 */
public interface MarathonCallbackHandler {

    boolean support(String event);

    Result handle(Long clusterId, String data);
}
