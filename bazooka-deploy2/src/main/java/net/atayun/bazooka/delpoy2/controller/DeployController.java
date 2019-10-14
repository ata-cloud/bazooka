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
package net.atayun.bazooka.delpoy2.controller;

import com.youyu.common.api.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.atayun.bazooka.deploy.biz.dto.deploy.DeployCountsDto;
import net.atayun.bazooka.deploy.biz.dto.flow.DeployingConfigInfoDto;
import net.atayun.bazooka.deploy.biz.dto.flow.DeployingFlowResultDto;
import net.atayun.bazooka.deploy.biz.enums.TimeGranularityEnum;
import net.atayun.bazooka.deploy.biz.service.deploy.DeployService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Ping
 * @date 2019-07-11
 */
@Api(description = "发布相关接口")
@RestController
@RequestMapping("/deploy")
public class DeployController {

    @Autowired
    private DeployService deployService;

    @ApiOperation(value = "获取正在发布的发布流")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "eventId", value = "事件Id", required = true, dataType = "long", paramType = "path"),
    })
    @GetMapping("/deploying-flow/{eventId}")
    public Result<DeployingFlowResultDto> getDeployingFlow(@PathVariable("eventId") Long eventId) {
        DeployingFlowResultDto result = deployService.getDeployingFlow(eventId);
        return Result.ok(result);
    }

    @ApiOperation(value = "获取正在发布的配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "服务ID", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "envId", value = "环境ID", required = true, dataType = "long", paramType = "path")
    })
    @GetMapping("/deploying-flow/config/{appId}/{envId}")
    public Result<DeployingConfigInfoDto> getDeployingConfigInfo(@PathVariable("appId") Long appId, @PathVariable("envId") Long envId) {
        DeployingConfigInfoDto dto = deployService.getDeployingConfigInfo(appId, envId);
        return Result.ok(dto);
    }

    @ApiOperation(value = "查询发布流程log")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deployId", value = "发布Id", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "deployFlowId", value = "发布流程Id", required = true, dataType = "long", paramType = "path"),
    })
    @GetMapping("/flow/log/{deployId}/{deployFlowId}")
    public Result<String> getDeployFlowLog(@PathVariable("deployId") Long deployId, @PathVariable("deployFlowId") Long deployFlowId) {
        String deployFlowLogDto = deployService.getDeployFlowLog(deployId, deployFlowId);
        return Result.ok(deployFlowLogDto);
    }

    @ApiOperation(value = "按日期统计项目下所有服务的发布次数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目Id", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "granularity", value = "时间粒度", required = true, dataType = "TimeGranularityEnum", paramType = "path")
    })
    @GetMapping("/counts/{projectId}/{granularity}")
    public Result<List<DeployCountsDto>> deployCountsByProject(@PathVariable("projectId") Long projectId, @PathVariable("granularity") TimeGranularityEnum timeGranularity) {
        List<DeployCountsDto> list = deployService.deployCountsByProject(projectId, timeGranularity);
        return Result.ok(list);
    }
}
