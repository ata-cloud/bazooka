package net.atayun.bazooka.rms.biz.controller;

import com.youyu.common.api.Result;
import net.atayun.bazooka.base.enums.AppOptEnum;
import net.atayun.bazooka.rms.api.api.RmsContainerApi;
import net.atayun.bazooka.rms.api.dto.NodeAvailableResourceDto;
import net.atayun.bazooka.rms.api.param.NodeContainerParam;
import net.atayun.bazooka.rms.biz.service.RmsContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Ping
 */
@RestController
public class RmsContainerController implements RmsContainerApi {

    @Autowired
    public RmsContainerService rmsContainerService;

    @Override
    public Result<NodeAvailableResourceDto> getEnvAvailableResource(@Validated @RequestBody List<Long> nodeIds) {
        NodeAvailableResourceDto nodeAvailableResourceDtoList = rmsContainerService.getNodeAvailableResource(nodeIds);
        return Result.ok(nodeAvailableResourceDtoList);
    }

    @Override
    public Result insert(@PathVariable("appId") Long appId, @PathVariable("opt") AppOptEnum opt, @Validated @RequestBody List<NodeContainerParam> nodeContainerParams) {
        rmsContainerService.insert(appId, opt, nodeContainerParams);
        return Result.ok();
    }
}
