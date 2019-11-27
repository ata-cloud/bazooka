package net.atayun.bazooka.deploy.biz.v2.controller;

import com.youyu.common.api.Result;
import net.atayun.bazooka.deploy.api.MarathonCallbackApi;
import net.atayun.bazooka.deploy.api.param.MarathonCallbackParam;
import net.atayun.bazooka.deploy.api.param.MarathonTaskFailureCallbackParam;
import net.atayun.bazooka.deploy.biz.v2.service.marathon.MarathonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ping
 */
@RestController
@RequestMapping("/deploy/marathon")
public class MarathonCallbackController implements MarathonCallbackApi {

    @Autowired
    private MarathonService marathonService;

    @Override
    public Result marathonCallback(@Validated @RequestBody MarathonCallbackParam marathonCallbackParam) {
        marathonService.marathonCallback(marathonCallbackParam);
        return Result.ok();
    }

    @Override
    public Result marathonTaskFailureCallback(MarathonTaskFailureCallbackParam marathonTaskFailureCallbackParam) {
        marathonService.marathonTaskFailureCallback(marathonTaskFailureCallbackParam);
        return Result.ok();
    }
}
