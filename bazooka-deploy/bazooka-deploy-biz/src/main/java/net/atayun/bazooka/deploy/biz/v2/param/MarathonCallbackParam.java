package net.atayun.bazooka.deploy.biz.v2.param;

import lombok.Getter;
import lombok.Setter;
import net.atayun.bazooka.base.enums.status.FinishStatusEnum;

/**
 * @author Ping
 */
@Getter
@Setter
public class MarathonCallbackParam {

    private String marathonDeploymentId;

    private String marathonDeploymentVersion;

    private FinishStatusEnum finishStatus;
}
