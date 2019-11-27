package net.atayun.bazooka.deploy.biz.v2.controller;

import com.youyu.common.api.Result;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.atayun.bazooka.deploy.biz.v2.dto.app.DeployCountsDto;
import net.atayun.bazooka.deploy.biz.v2.dto.app.DeployingConfigInfoDto;
import net.atayun.bazooka.deploy.biz.v2.dto.app.DeployingFlowResultDto;
import net.atayun.bazooka.deploy.biz.v2.enums.TimeGranularityEnum;
import net.atayun.bazooka.deploy.biz.v2.service.app.FlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Ping
 */
@RestController
@RequestMapping("/deploy")
public class FlowController {

    @Autowired
    private FlowService flowService;

    @ApiOperation(value = "获取正在发布的发布流")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "optId", value = "操作Id", required = true, dataType = "long", paramType = "path"),
    })
    @GetMapping("/deploying-flow/{optId}")
    public Result<DeployingFlowResultDto> getDeployingFlow(@PathVariable("optId") Long optId) {
        DeployingFlowResultDto result = flowService.getDeployingFlow(optId);
        return Result.ok(result);
    }

    @ApiOperation(value = "获取正在发布的配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "服务ID", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "envId", value = "环境ID", required = true, dataType = "long", paramType = "path")
    })
    @GetMapping("/deploying-flow/config/{appId}/{envId}")
    public Result<DeployingConfigInfoDto> getDeployingConfigInfo(@PathVariable("appId") Long appId, @PathVariable("envId") Long envId) {
        DeployingConfigInfoDto dto = flowService.getDeployingConfigInfo(appId, envId);
        return Result.ok(dto);
    }

    @ApiOperation(value = "查询发布流程log")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "optId", value = "操作Id", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "stepId", value = "步骤Id", required = true, dataType = "long", paramType = "path"),
    })
    @GetMapping("/flow/log/{optId}/{stepId}")
    public Result<String> getStepLog(@PathVariable("optId") Long optId, @PathVariable("stepId") Long stepId) {
        String deployFlowLogDto = flowService.getStepLog(optId, stepId);
        return Result.ok(deployFlowLogDto);
    }

    @ApiOperation(value = "按日期统计项目下所有服务的发布次数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目Id", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "granularity", value = "时间粒度", required = true, dataType = "TimeGranularityEnum", paramType = "path")
    })
    @GetMapping("/counts/{projectId}/{granularity}")
    public Result<List<DeployCountsDto>> deployCountsByProject(@PathVariable("projectId") Long projectId, @PathVariable("granularity") TimeGranularityEnum timeGranularity) {
        List<DeployCountsDto> list = flowService.deployCountsByProject(projectId, timeGranularity);
        return Result.ok(list);
    }
}
