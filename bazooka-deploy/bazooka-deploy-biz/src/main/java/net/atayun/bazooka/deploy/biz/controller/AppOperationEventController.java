/*
 *    Copyright 2018-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.atayun.bazooka.deploy.biz.controller;

import net.atayun.bazooka.deploy.api.AppOperationEventApi;
import net.atayun.bazooka.deploy.api.dto.AppEventOperateDto;
import net.atayun.bazooka.deploy.api.dto.AppRunningEventDto;
import net.atayun.bazooka.deploy.api.param.AppOperationEventParam;
import net.atayun.bazooka.deploy.api.param.MarathonCallbackParam;
import net.atayun.bazooka.deploy.api.param.MarathonTaskFailureCallbackParam;
import net.atayun.bazooka.deploy.biz.dto.app.AppEventOperateWithStatusDto;
import net.atayun.bazooka.deploy.biz.dto.app.AppOperateEventHistoryDto;
import net.atayun.bazooka.deploy.biz.dto.app.AppOperateEventHistoryMarathonDto;
import net.atayun.bazooka.deploy.biz.dto.app.AppOperateEventLogDto;
import net.atayun.bazooka.deploy.biz.param.app.AppOperateEventHistoryMarathonParam;
import net.atayun.bazooka.deploy.biz.param.app.AppOperateEventHistoryParam;
import net.atayun.bazooka.deploy.biz.service.app.AppOperationEventService;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Ping
 * @date 2019-07-26
 */
@RestController
public class AppOperationEventController implements AppOperationEventApi {

    @Autowired
    private AppOperationEventService appOperationEventService;

    @Override
    public Result<AppEventOperateDto> operate(@Validated @RequestBody AppOperationEventParam appOperationEventParam) {
        Long eventId = appOperationEventService.operationEvent(appOperationEventParam);
        return Result.ok(new AppEventOperateDto(appOperationEventParam.getEvent(), eventId));
    }

    @ApiOperation("所有操作记录")
    @PostMapping("/operate/history")
    public Result<PageData<AppOperateEventHistoryDto>> getAppOperateEventHistory(@Validated @RequestBody AppOperateEventHistoryParam pageParam) {
        PageData<AppOperateEventHistoryDto> pageData = appOperationEventService.getAppOperateEventHistory(pageParam);
        return Result.ok(pageData);
    }

    @ApiOperation("所有操作记录(Marathon相关)")
    @PostMapping("/operate/history/marathon")
    public Result<PageData<AppOperateEventHistoryMarathonDto>> getAppOperateEventHistoryMarathon(@Validated @RequestBody AppOperateEventHistoryMarathonParam pageParam) {
        PageData<AppOperateEventHistoryMarathonDto> pageData = appOperationEventService.getAppOperateEventHistoryMarathon(pageParam);
        return Result.ok(pageData);
    }

    @ApiOperation("操作记录详情(Marathon相关)")
    @ApiImplicitParam(name = "eventId", value = "事件Id", required = true, dataType = "long", paramType = "path")
    @GetMapping("/operate/history/marathon/detail/{eventId}")
    public Result<String> getAppOperateEventHistoryMarathonDetail(@PathVariable("eventId") Long eventId) {
        String marathonConfig = appOperationEventService.getAppOperateEventHistoryMarathonDetail(eventId);
        return Result.ok(marathonConfig);
    }

    @ApiOperation("操作记录详情")
    @ApiImplicitParam(name = "eventId", value = "事件Id", required = true, dataType = "long", paramType = "path")
    @GetMapping("/operate/log/{eventId}")
    public Result<List<AppOperateEventLogDto>> getAppOperateEventLog(@PathVariable("eventId") Long eventId) {
        List<AppOperateEventLogDto> list = appOperationEventService.getAppOperateEventLog(eventId);
        return Result.ok(list);
    }

    @Override
    public Result marathonCallback(@Validated @RequestBody MarathonCallbackParam marathonCallbackParam) {
        appOperationEventService.marathonCallback(marathonCallbackParam.getMarathonDeploymentId(),
                marathonCallbackParam.getMarathonDeploymentVersion(),
                marathonCallbackParam.getFinishStatus());
        return Result.ok();
    }

    @Override
    public Result marathonTaskFailureCallback(MarathonTaskFailureCallbackParam marathonTaskFailureCallbackParam) {
        appOperationEventService.marathonTaskFailureCallback(marathonTaskFailureCallbackParam);
        return Result.ok();
    }

    @Override
    public Result<List<AppRunningEventDto>> getAppRunningEvent(@PathVariable("appId") Long appId) {
        List<AppRunningEventDto> events = appOperationEventService.getAppRunningEvent(appId);
        return Result.ok(events);
    }

    @ApiOperation("操作记录状态")
    @ApiImplicitParam(name = "eventId", value = "事件Id", required = true, dataType = "long", paramType = "path")
    @GetMapping("/operate/status/{eventId}")
    public Result<AppEventOperateWithStatusDto> getEventStatus(@PathVariable("eventId") Long eventId) {
        AppEventOperateWithStatusDto dto = appOperationEventService.getEventStatus(eventId);
        return Result.ok(dto);
    }
}
