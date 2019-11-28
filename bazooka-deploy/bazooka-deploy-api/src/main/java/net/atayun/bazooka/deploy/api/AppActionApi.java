package net.atayun.bazooka.deploy.api;

import com.youyu.common.api.Result;
import io.swagger.annotations.ApiOperation;
import net.atayun.bazooka.deploy.api.dto.AppRunningEventDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author Ping
 */
@RequestMapping("/deploy/app")
public interface AppActionApi {

    @ApiOperation(value = "服务正在运行的事件")
    @GetMapping("/app-running-event/{appId}")
    Result<List<AppRunningEventDto>> getAppRunningEvent(@PathVariable("appId") Long appId);
}
