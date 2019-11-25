package net.atayun.bazooka.deploy.biz.v2.controller.app;

import com.youyu.common.api.Result;
import net.atayun.bazooka.deploy.biz.v2.param.StepCallbackParam;
import net.atayun.bazooka.deploy.biz.v2.service.jenkins.JenkinsService;
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
@RequestMapping("/deploy/jenkins")
public class JenkinsCallbackController {

    @Autowired
    private JenkinsService jenkinsService;

    @PostMapping("/step/callback")
    public Result stepCallback(@Validated @RequestBody StepCallbackParam stepCallbackParam) {
        jenkinsService.stepCallback(stepCallbackParam);
        return Result.ok();
    }
}
