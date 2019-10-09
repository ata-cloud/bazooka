package net.atayun.bazooka.deploy.api.param;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Ping
 */
@Getter
@Setter
public class MarathonTaskFailureCallbackParam {

    private Long clusterId;

    private String marathonServiceId;

    private String marathonDeploymentVersion;

    private String message;
}
