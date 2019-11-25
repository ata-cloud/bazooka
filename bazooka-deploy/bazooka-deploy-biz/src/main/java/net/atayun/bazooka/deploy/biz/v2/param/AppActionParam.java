package net.atayun.bazooka.deploy.biz.v2.param;

import lombok.Getter;
import lombok.Setter;
import net.atayun.bazooka.deploy.biz.v2.enums.AppOptEnum;

/**
 * @author Ping
 */
@Getter
@Setter
public class AppActionParam {

    private AppOptEnum appAction;

    private boolean useDefaultFlow;

}
