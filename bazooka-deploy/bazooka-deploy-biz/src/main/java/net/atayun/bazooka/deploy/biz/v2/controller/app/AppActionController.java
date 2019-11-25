package net.atayun.bazooka.deploy.biz.v2.controller.app;

import com.youyu.common.api.Result;
import net.atayun.bazooka.deploy.biz.v2.dto.app.AppActionDto;
import net.atayun.bazooka.deploy.biz.v2.param.AppActionParam;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ping
 */
@RestController
public class AppActionController {

    @Autowired
    private AppActionService appActionService;

    public Result<AppActionDto> action(AppActionParam appActionParam) {
        AppActionDto appActionDto = appActionService.action(appActionParam);
        return Result.ok(appActionDto);
    }
}
