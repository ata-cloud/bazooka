package net.atayun.bazooka.deploy.biz.v2.service.app;

import net.atayun.bazooka.deploy.biz.v2.dto.app.DeployCountsDto;
import net.atayun.bazooka.deploy.biz.v2.dto.app.DeployingConfigInfoDto;
import net.atayun.bazooka.deploy.biz.v2.dto.app.DeployingFlowResultDto;
import net.atayun.bazooka.deploy.biz.v2.enums.TimeGranularityEnum;

import java.util.List;

/**
 * @author Ping
 */
public interface FlowService {

    DeployingFlowResultDto getDeployingFlow(Long optId);

    DeployingConfigInfoDto getDeployingConfigInfo(Long appId, Long envId);

    String getStepLog(Long optId, Long stepId);

    List<DeployCountsDto> deployCountsByProject(Long projectId, TimeGranularityEnum timeGranularity);
}
