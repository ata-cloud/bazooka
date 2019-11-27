package net.atayun.bazooka.deploy.biz.v2.service.app.impl;

import com.youyu.common.exception.BizException;
import com.youyu.common.utils.YyBeanUtils;
import net.atayun.bazooka.base.enums.deploy.DeployModeEnum;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptCounts;
import net.atayun.bazooka.deploy.biz.v2.dto.app.AppOptCountsDto;
import net.atayun.bazooka.deploy.biz.v2.dto.app.DeployingConfigInfoDto;
import net.atayun.bazooka.deploy.biz.v2.dto.app.DeployingFlowDto;
import net.atayun.bazooka.deploy.biz.v2.dto.app.DeployingFlowResultDto;
import net.atayun.bazooka.deploy.biz.v2.enums.TimeGranularityEnum;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppOptService;
import net.atayun.bazooka.deploy.biz.v2.service.app.FlowService;
import net.atayun.bazooka.deploy.biz.v2.service.app.FlowStepService;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.feign.AppApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;
import static net.atayun.bazooka.deploy.biz.v2.constant.DeployResultCodeConstants.ILLEGAL_EVENT_ID_ERR_CODE;

/**
 * @author Ping
 */
@Service
public class FlowServiceImpl implements FlowService {

    @Autowired
    private AppOptService appOptService;

    @Autowired
    private FlowStepService flowStepService;

    @Override
    public DeployingFlowResultDto getDeployingFlow(Long optId) {
        AppOpt appOpt = appOptService.selectById(optId);
        if (appOpt == null) {
            throw new BizException(ILLEGAL_EVENT_ID_ERR_CODE, "无效事件ID");
        }
        List<DeployingFlowDto> list = flowStepService.getDeployingFlow(optId);
        DeployingFlowResultDto result = new DeployingFlowResultDto();
        result.setStatus(appOpt.getStatus());
        result.setFlows(list);
        return result;
    }

    @Override
    public DeployingConfigInfoDto getDeployingConfigInfo(Long appId, Long envId) {
        AppOpt appOpt = appOptService.isProcessing(appId, envId);
        if (appOpt == null) {
            return null;
        }
        DeployingConfigInfoDto deployingConfigInfoDto = new DeployingConfigInfoDto();
        deployingConfigInfoDto.setEventId(appOpt.getId());
        deployingConfigInfoDto.setDeployConfigId(appOpt.getDeployConfigId());
        deployingConfigInfoDto.setBranch(appOpt.getBranch());
        deployingConfigInfoDto.setDockerImageTag(appOpt.getDockerImageTag());
        AppDeployConfigDto data = getBean(AppApi.class).getAppDeployConfigInfoById(appOpt.getDeployConfigId())
                .ifNotSuccessThrowException()
                .getData();
        DeployModeEnum deployMode = data.getDeployMode();
        deployingConfigInfoDto.setDeployMode(deployMode);
        return deployingConfigInfoDto;
    }

    @Override
    public String getStepLog(Long optId, Long stepId) {
        AppOptFlowStep appOptFlowStep = flowStepService.selectById(stepId);
        return "";
    }

    @Override
    public List<AppOptCountsDto> deployCountsByProject(Long projectId, TimeGranularityEnum timeGranularity) {
        LocalDateTime leftDatetime = timeGranularity.getLeftDatetime();
        List<AppOptCounts> appOptCounts = appOptService.appOptCountsByProject(projectId, leftDatetime);
        if (CollectionUtils.isEmpty(appOptCounts)) {
            return new ArrayList<>();
        }
        return appOptCounts.stream()
                .map(appOptCount -> {
                    AppOptCountsDto appOptCountsDto = new AppOptCountsDto();
                    YyBeanUtils.copyProperties(appOptCount, appOptCountsDto);
                    return appOptCountsDto;
                })
                .collect(Collectors.toList());
    }
}
