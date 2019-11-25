package net.atayun.bazooka.deploy.biz.v2.param;

import lombok.Getter;
import lombok.Setter;
import net.atayun.bazooka.deploy.biz.constants.JenkinsCallbackConstants;

import java.util.Map;

/**
 * @author Ping
 */
@Getter
@Setter
public class StepCallbackParam {

    public static final String CUSTOM_KEY_OPT_ID = "optId";
    public static final String CUSTOM_KEY_STEP_ID = "stepId";

    private String jobName;

    private Integer jobBuildNumber;

    /**
     * SUCCESS,UNSTABLE,FAILURE,NOT_BUILT,ABORTED
     *
     * @see JenkinsCallbackConstants
     */
    private String result;

    private Map<String, String> custom;
}
