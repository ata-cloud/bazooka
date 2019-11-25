package net.atayun.bazooka.deploy.biz.v2.controller.app;

import com.youyu.common.api.Result;
import net.atayun.bazooka.deploy.biz.v2.param.MarathonCallbackParam;
import net.atayun.bazooka.deploy.biz.v2.service.marathon.MarathonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ping
 */
@RestController
@RequestMapping("/deploy/marathon")
public class MarathonCallbackController {

    @Autowired
    private MarathonService marathonService;

    @PostMapping("/callback")
    public Result marathonCallback(@Validated @RequestBody MarathonCallbackParam marathonCallbackParam) {
        marathonService.marathonCallback(marathonCallbackParam);
        return Result.ok();
    }
}
