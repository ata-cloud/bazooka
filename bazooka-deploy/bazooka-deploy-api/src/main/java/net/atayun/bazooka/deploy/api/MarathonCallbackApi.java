package net.atayun.bazooka.deploy.api;

import com.youyu.common.api.Result;
import io.swagger.annotations.ApiOperation;
import net.atayun.bazooka.deploy.api.param.MarathonCallbackParam;
import net.atayun.bazooka.deploy.api.param.MarathonTaskFailureCallbackParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Ping
 */
@RequestMapping("/deploy/marathon")
public interface MarathonCallbackApi {

    @PostMapping("/callback")
    Result marathonCallback(@Validated @RequestBody MarathonCallbackParam marathonCallbackParam);

    @ApiOperation(value = "处理marathon回调")
    @PostMapping("/task/failure/callback")
    Result marathonTaskFailureCallback(@Validated @RequestBody MarathonTaskFailureCallbackParam marathonTaskFailureCallbackParam);
}
