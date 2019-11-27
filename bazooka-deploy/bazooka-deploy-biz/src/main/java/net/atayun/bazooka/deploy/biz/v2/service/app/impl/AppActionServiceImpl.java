package net.atayun.bazooka.deploy.biz.v2.service.app.impl;

import com.alibaba.fastjson.JSONObject;
import com.youyu.common.api.PageData;
import com.youyu.common.enums.IsDeleted;
import com.youyu.common.exception.BizException;
import com.youyu.common.utils.YyBeanUtils;
import net.atayun.bazooka.base.enums.AppOptEnum;
import net.atayun.bazooka.deploy.api.dto.AppRunningEventDto;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptHis;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptWithPlatform;
import net.atayun.bazooka.deploy.biz.v2.dto.app.*;
import net.atayun.bazooka.deploy.biz.v2.enums.AppOptStatusEnum;
import net.atayun.bazooka.deploy.biz.v2.param.AppActionParam;
import net.atayun.bazooka.deploy.biz.v2.param.AppOptHisPlatformParam;
import net.atayun.bazooka.deploy.biz.v2.param.AppOptHisParam;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppActionService;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppOptService;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppStatusOpt;
import net.atayun.bazooka.deploy.biz.v2.service.app.FlowStepService;
import net.atayun.bazooka.deploy.biz.v2.service.app.opt.AppOptWorker;
import net.atayun.bazooka.deploy.biz.v2.service.app.threadpool.AppActionThreadPool;
import net.atayun.bazooka.pms.api.dto.AppInfoDto;
import net.atayun.bazooka.pms.api.dto.PmsAppDeployStatusDto;
import net.atayun.bazooka.pms.api.feign.AppApi;
import net.atayun.bazooka.rms.api.RmsDockerImageApi;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.dto.ClusterConfigDto;
import net.atayun.bazooka.rms.api.dto.EnvDto;
import net.atayun.bazooka.rms.api.dto.RmsDockerImageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;
import static net.atayun.bazooka.deploy.biz.v2.constant.DeployResultCodeConstants.APP_IS_DEPLOYING_ERR_CODE;

/**
 * @author Ping
 */
@Service
public class AppActionServiceImpl implements AppActionService {

    @Autowired
    private AppOptService appOptService;

    @Autowired
    private FlowStepService flowStepService;

    @Autowired
    private EnvApi envApi;

    @Autowired
    private AppApi appApi;

    @Override
    public AppActionDto action(AppActionParam appActionParam) {
        PmsAppDeployStatusDto pmsAppDeployStatus = appApi.getAppDeployStatus(appActionParam.getAppId(), appActionParam.getEnvId())
                .ifNotSuccessThrowException()
                .getData();
        @NotNull AppOptEnum appOptEnum = appActionParam.getEvent();
        if (appOptEnum.isMutex() && pmsAppDeployStatus != null && pmsAppDeployStatus.getDeploying()) {
            throw new BizException(APP_IS_DEPLOYING_ERR_CODE,
                    String.format("服务正在执行 [%s] 操作", pmsAppDeployStatus.getDeployType().getDescription()));
        }
        AppStatusOpt.updateAppStatus(appActionParam.getAppId(), appActionParam.getEnvId(), true, appOptEnum);
        convertOpt(appActionParam);
        AppOpt appOpt = appOptService.saveOpt(appActionParam);
        AppActionThreadPool.execute(() -> new AppOptWorker(appOpt).doWork());
        return new AppActionDto(appOpt.getId(), appOptEnum);
    }

    @Deprecated
    private void convertOpt(AppActionParam appActionParam) {
        @NotNull AppOptEnum appOptEnum = appActionParam.getEvent();
        if (appOptEnum == AppOptEnum.DEPLOY) {
            JSONObject jsonObject = JSONObject.parseObject(appActionParam.getDetail());
            if (jsonObject.getString("dockerImageTag") != null) {
                appActionParam.setEvent(AppOptEnum.IMAGE_DEPLOY);
            } else {
                EnvDto envDto = envApi.get(appActionParam.getEnvId()).ifNotSuccessThrowException().getData();
                String clusterType = envDto.getClusterType();
                switch (clusterType) {
                    case "0":
                        appActionParam.setEvent(AppOptEnum.MARATHON_BUILD_DEPLOY);
                        break;
                    case "2":
                        appActionParam.setEvent(AppOptEnum.NODE_BUILD_DEPLOY);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public List<AppRunningEventDto> getAppRunningEvent(Long appId) {
        List<AppOpt> appOpts = appOptService.selectByAppIdAndStatus(appId, AppOptStatusEnum.PROCESS);
        if (CollectionUtils.isEmpty(appOpts)) {
            return new ArrayList<>();
        }
        return appOpts.stream()
                .map(entity -> {
                    AppRunningEventDto dto = new AppRunningEventDto();
                    dto.setEnvId(entity.getEnvId());
                    dto.setEvent(entity.getOpt());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public PageData<AppOptHisDto> getAppOptHis(AppOptHisParam pageData) {
        List<AppOptHis> entities = appOptService.getAppOptHis(pageData);
        if (CollectionUtils.isEmpty(entities)) {
            pageData.setRows(new ArrayList<>());
            return pageData;
        }
        List<AppOptHisDto> collect = entities.stream()
                .map(appOptHis -> {
                    AppOptHisDto appOptHisDto = new AppOptHisDto();
                    YyBeanUtils.copyProperties(appOptHis, appOptHisDto);
                    appOptHisDto.setEvent(appOptHis.getEvent().getDescription());
                    appOptHisDto.setStatus(appOptHis.getStatus());
                    return appOptHisDto;
                }).collect(Collectors.toList());
        pageData.setRows(collect);
        return pageData;
    }

    @Override
    public PageData<AppOptHisPlatformDto> getAppOptHisPlatform(AppOptHisPlatformParam pageData) {
        List<AppOptWithPlatform> entities = appOptService.getAppOptHisPlatform(pageData);
        if (CollectionUtils.isEmpty(entities)) {
            pageData.setRows(new ArrayList<>());
            return pageData;
        }
        ClusterConfigDto clusterConfig = envApi.getEnvConfiguration(pageData.getEnvId())
                .ifNotSuccessThrowException()
                .getData();
        String dockerHubUrl = clusterConfig.getDockerHubUrl();
        AppInfoDto appInfo = appApi.getAppInfoById(pageData.getAppId())
                .ifNotSuccessThrowException()
                .getData();
        String dockerImageName = appInfo.getDockerImageName();
        List<RmsDockerImageDto> imageTags = getBean(RmsDockerImageApi.class).getDockerImageListByRegistryAndImageName(dockerHubUrl, dockerImageName)
                .ifNotSuccessThrowException()
                .getData();

        AppOpt rollbackEntity = getRollback(pageData.getAppId(), pageData.getEnvId());
        Long templateEventId = null;
        if (rollbackEntity != null) {
            templateEventId = rollbackEntity.getTemplateEventId();
        }
        Long finalTemplateEventId = templateEventId;
        List<AppOptHisPlatformDto> resultPageRow = entities.stream()
                .map(appOptWithPlatform -> {
                    AppOptHisPlatformDto dto = new AppOptHisPlatformDto();
                    dto.setEvent(appOptWithPlatform.getEvent().getDescription());
                    dto.setEventId(appOptWithPlatform.getEventId());
                    dto.setStatus(appOptWithPlatform.getStatus().getDescription());
                    dto.setStatusCode(appOptWithPlatform.getStatus());
                    dto.setVersion(appOptWithPlatform.getAppDeployVersion());
//                    String platformConfig = appOptWithPlatform.getAppDeployConfig();
//                    App app = ModelUtils.GSON.fromJson(marathonConfig, App.class);
//                    String image = app.getContainer().getDocker().getImage();
//                    String[] split = image.split(":");
                    String imageTag = "";
                    dto.setImageTag(imageTag);
                    Optional<RmsDockerImageDto> imageDtoOptional = imageTags.stream()
                            .filter(rmsDockerImageDto -> Objects.equals(imageTag, rmsDockerImageDto.getImageTag()))
                            .findAny();
                    IsDeleted isDelete = imageDtoOptional
                            .map(RmsDockerImageDto::getIsDeleted)
                            .orElse(IsDeleted.DELETED);
                    dto.setImageIsDelete(isDelete == IsDeleted.DELETED);
                    dto.setGitCommitId(imageDtoOptional.map(RmsDockerImageDto::getGitCommitId).orElse(""));
                    dto.setGitCommitTime(imageDtoOptional.map(RmsDockerImageDto::getGitCommitTime).orElse(null));
                    dto.setIsTheLast(false);
                    dto.setIsRollback(finalTemplateEventId != null && appOptWithPlatform.getEventId().compareTo(finalTemplateEventId) == 0);
                    return dto;
                })
                .collect(Collectors.toList());

        if (pageData.getPageNum() == 1 && !CollectionUtils.isEmpty(resultPageRow)) {
            resultPageRow.get(0).setIsTheLast(true);
        }
        pageData.setRows(resultPageRow);
        return pageData;
    }

    @Override
    public String getAppOptHisPlatformDetail(Long optId) {
        AppOpt appOpt = appOptService.selectById(optId);
        if (appOpt == null) {
            return "";
        }
        if (appOpt.getOpt() == AppOptEnum.NODE_BUILD_DEPLOY) {
            return "{\"cmd\": \"" + appOpt.getAppDeployConfig() + "\"}";
        }
        return appOpt.getAppDeployConfig();
    }

    @Override
    public List<AppOptLogDto> getAppOptLog(Long optId) {
        List<AppOptFlowStep> steps = flowStepService.selectByOptId(optId);
        return steps.stream().map(step -> new AppOptLogDto(step.getStep(), "")).collect(Collectors.toList());
    }

    @Override
    public AppOptWithStatusDto getOptStatus(Long optId) {
        AppOpt appOpt = appOptService.selectById(optId);
        AppOptWithStatusDto dto = new AppOptWithStatusDto();
        dto.setEventId(appOpt.getId());
        dto.setEvent(appOpt.getOpt());
        dto.setStatus(appOpt.getStatus());
        return dto;
    }

    private AppOpt getRollback(Long appId, Long envId) {
        return appOptService.selectByStatusAndOpt(appId, envId, AppOptStatusEnum.PROCESS, AppOptEnum.ROLLBACK);
    }
}
