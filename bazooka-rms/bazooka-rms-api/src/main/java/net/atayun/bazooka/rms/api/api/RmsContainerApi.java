package net.atayun.bazooka.rms.api.api;

import com.youyu.common.api.Result;
import net.atayun.bazooka.base.enums.AppOptEnum;
import net.atayun.bazooka.rms.api.dto.NodeAvailableResourceDto;
import net.atayun.bazooka.rms.api.param.NodeContainerParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author Ping
 */
@RequestMapping("/node-container")
public interface RmsContainerApi {

    @PostMapping("/resource")
    Result<NodeAvailableResourceDto> getEnvAvailableResource(@Validated @RequestBody List<Long> nodeIds);

    @PostMapping("/insert/{envId}/{appId}/{opt}")
    Result insert(@PathVariable("envId") Long envId,
                  @PathVariable("appId") Long appId,
                  @PathVariable("opt") AppOptEnum opt,
                  @Validated @RequestBody List<NodeContainerParam> nodeContainerParams);
}
