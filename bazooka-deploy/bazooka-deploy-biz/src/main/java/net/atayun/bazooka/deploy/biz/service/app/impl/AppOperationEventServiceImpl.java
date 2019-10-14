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
package net.atayun.bazooka.deploy.biz.service.app.impl;

import com.alibaba.fastjson.JSONObject;
import net.atayun.bazooka.combase.bean.StrategyNumBean;
import net.atayun.bazooka.combase.enums.deploy.AppOperationEnum;
import net.atayun.bazooka.combase.enums.deploy.AppOperationEventLogTypeEnum;
import net.atayun.bazooka.combase.enums.status.FinishStatusEnum;
import net.atayun.bazooka.deploy.api.dto.AppRunningEventDto;
import net.atayun.bazooka.deploy.api.param.AppOperationEventParam;
import net.atayun.bazooka.deploy.biz.dal.dao.app.AppOperationEventMapper;
import net.atayun.bazooka.deploy.biz.dal.entity.app.AppOperateEventHistoryEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.app.AppOperationEventEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.app.AppOperationEventMarathonEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.app.EventWithMarathonEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.deploy.DeployEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;
import net.atayun.bazooka.deploy.biz.dto.app.*;
import net.atayun.bazooka.deploy.biz.enums.flow.DeployFlowEnum;
import net.atayun.bazooka.deploy.biz.enums.status.AppOperationEventStatusEnum;
import net.atayun.bazooka.deploy.biz.log.AppOperationEventLog;
import net.atayun.bazooka.deploy.biz.log.LogConcat;
import net.atayun.bazooka.deploy.biz.param.app.AppOperateEventHistoryMarathonParam;
import net.atayun.bazooka.deploy.biz.param.app.AppOperateEventHistoryParam;
import net.atayun.bazooka.deploy.biz.service.app.AppOperationEventDetailService;
import net.atayun.bazooka.deploy.biz.service.app.AppOperationEventMarathonService;
import net.atayun.bazooka.deploy.biz.service.app.AppOperationEventService;
import net.atayun.bazooka.deploy.biz.service.app.event.AbstractAppOperationEvent;
import net.atayun.bazooka.deploy.biz.service.app.event.AppDeployOperationEvent;
import net.atayun.bazooka.deploy.biz.service.app.event.AppRollbackOperationEventPoJo;
import net.atayun.bazooka.deploy.biz.service.deploy.DeployService;
import net.atayun.bazooka.deploy.biz.service.flow.DeployFlowService;
import net.atayun.bazooka.deploy.biz.service.status.AppStatusOpt;
import net.atayun.bazooka.deploy.biz.service.status.EventStatusOpt;
import net.atayun.bazooka.pms.api.dto.AppInfoDto;
import net.atayun.bazooka.pms.api.dto.PmsAppDeployStatusDto;
import net.atayun.bazooka.pms.api.feign.AppApi;
import net.atayun.bazooka.pms.api.RmsDockerImageApi;
import net.atayun.bazooka.pms.api.api.EnvApi;
import net.atayun.bazooka.pms.api.dto.ClusterConfigDto;
import net.atayun.bazooka.pms.api.dto.RmsDockerImageDto;
import com.youyu.common.api.PageData;
import com.youyu.common.enums.IsDeleted;
import com.youyu.common.exception.BizException;
import com.youyu.common.service.AbstractService;
import com.youyu.common.utils.YyBeanUtils;
import lombok.extern.slf4j.Slf4j;
import mesosphere.client.common.ModelUtils;
import mesosphere.marathon.client.model.v2.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

import static net.atayun.bazooka.combase.bean.SpringContextBean.getBean;
import static net.atayun.bazooka.deploy.biz.constants.DeployResultCodeConstants.APP_DEPLOY_MARATHON_CALLBACK_ERR_CODE;
import static net.atayun.bazooka.deploy.biz.constants.DeployResultCodeConstants.APP_IS_DEPLOYING_ERR_CODE;

/**
 * @author Ping
 * @date 2019-07-25
 */
@Slf4j
@Service
public class AppOperationEventServiceImpl
        extends AbstractService<Long, AppOperationEventDto, AppOperationEventEntity, AppOperationEventMapper>
        implements AppOperationEventService, AppStatusOpt, EventStatusOpt {

    @Autowired
    private AppApi appApi;

    @Autowired
    private AppOperationEventMarathonService appOperationEventMarathonService;

    @Autowired
    private AppOperationEventDetailService appOperationEventDetailService;

    @Autowired
    private RmsDockerImageApi rmsDockerImageApi;

    @Autowired
    private EnvApi envApi;

    @Autowired
    private AppOperationEventLog appOperationEventLog;

    @Autowired
    private DeployFlowService deployFlowService;

    @Autowired
    private DeployService deployService;

    /**
     * 事件操作
     *
     * @param appOperationEventParam 参数
     * @return 事件ID 以及sub id
     */
    @Override
    public Long operationEvent(AppOperationEventParam appOperationEventParam) {
        eventFilter(appOperationEventParam);
        @NotNull Long appId = appOperationEventParam.getAppId();
        @NotNull Long envId = appOperationEventParam.getEnvId();
        PmsAppDeployStatusDto pmsAppDeployStatus = appApi.getAppDeployStatus(appId, envId)
                .ifNotSuccessThrowException()
                .getData();
        @NotNull AppOperationEnum event = appOperationEventParam.getEvent();
        if (event.getAppRunStatus() && pmsAppDeployStatus != null && pmsAppDeployStatus.getDeploying()) {
            throw new BizException(APP_IS_DEPLOYING_ERR_CODE,
                    String.format("服务正在执行 [%s] 操作", pmsAppDeployStatus.getDeployType().getDescription()));
        }
        updateAppStatus(appId, envId, true, event);
        Long eventId = null;
        try {
            AppOperationEventEntity appOperationEventEntity = new AppOperationEventEntity();
            appOperationEventEntity.setAppId(appId);
            appOperationEventEntity.setEnvId(envId);
            appOperationEventEntity.setEvent(event);
            appOperationEventEntity.setStatus(AppOperationEventStatusEnum.PROCESS);
            appOperationEventEntity.setDetail(appOperationEventParam.getDetail());
            getMapper().insertSelective(appOperationEventEntity);

            eventId = appOperationEventEntity.getId();

            AbstractAppOperationEvent appOperationEvent = StrategyNumBean.getBeanInstance(AbstractAppOperationEvent.class, event.name());

            String eventRemark = appOperationEvent.getEventRemark(appOperationEventParam);
            appOperationEventDetailService.insertByParam(eventId, eventRemark);
            getBean(AppOperationEventLog.class).save(eventId, event.getLogType(), eventRemark);

            appOperationEvent.doWork(appOperationEventParam, eventId);

            return eventId;
        } catch (Throwable throwable) {
            updateAppStatus(appId, envId, false, event);
            if (eventId != null) {
                updateEventStatus(eventId, AppOperationEventStatusEnum.FAILURE);
            }
            throw throwable;
        }
    }

    private void eventFilter(AppOperationEventParam appOperationEventParam) {

    }

    /**
     * marathon callback
     *
     * @param marathonDeploymentId      marathonDeploymentId
     * @param marathonDeploymentVersion marathonDeploymentVersion
     * @param status                    status
     */
    @Override
    public void marathonCallback(String marathonDeploymentId, String marathonDeploymentVersion, FinishStatusEnum status) {
        AppOperationEventMarathonEntity appOperationEventMarathonEntity =
                appOperationEventMarathonService.selectByMarathonInfo(marathonDeploymentId, marathonDeploymentVersion);
        if (appOperationEventMarathonEntity == null) {
            log.warn("未匹配的marathon事件[marathonDeploymentId: {}, marathonDeploymentVersion: {}]",
                    marathonDeploymentId, marathonDeploymentVersion);
            return;
        }
        AppOperationEventEntity appOperationEventEntity = getMapper().selectByPrimaryKey(appOperationEventMarathonEntity.getEventId());
        //如果将事件操作状态已经通过其它方式置为失败(例如: ata服务重启), 则无需继续进行后续步骤
        if (appOperationEventEntity.getStatus() == AppOperationEventStatusEnum.FAILURE) {
            return;
        }

        Long eventId = appOperationEventEntity.getId();
        AppOperationEnum event = appOperationEventEntity.getEvent();
        AppOperationEventStatusEnum eventStatus = status == FinishStatusEnum.SUCCESS ?
                AppOperationEventStatusEnum.SUCCESS :
                AppOperationEventStatusEnum.FAILURE;
        LogConcat logConcat = new LogConcat();
        try {
            if (event == AppOperationEnum.DEPLOY) {
                AppDeployOperationEvent appDeployOperationEvent = (AppDeployOperationEvent) StrategyNumBean.getBeanInstance(AbstractAppOperationEvent.class, AppOperationEnum.DEPLOY.name());
                appDeployOperationEvent.marathonCallbackForDeploy(eventId, status);
            }
        } catch (Throwable throwable) {
            eventStatus = AppOperationEventStatusEnum.FAILURE;
            logConcat.concat(throwable);
            throw new BizException(APP_DEPLOY_MARATHON_CALLBACK_ERR_CODE, "发布Marathon回调异常", throwable);
        } finally {
            updateEventStatus(eventId, eventStatus);
            updateAppStatus(appOperationEventEntity.getAppId(), appOperationEventEntity.getEnvId(), false, event);

            logConcat.concat("发布ID: " + marathonDeploymentId);
            logConcat.concat("发布版本: " + marathonDeploymentVersion);
            logConcat.concat("发布状态: " + status);
            getBean(AppOperationEventLog.class).saveAndMerge(eventId, event.getLogType(), 2, logConcat.get());
        }
    }

    /**
     * id 查询
     *
     * @param eventId eventId
     * @return AppOperationEventEntity
     */
    @Override
    public AppOperationEventEntity selectById(Long eventId) {
        return getMapper().selectByPrimaryKey(eventId);
    }

    /**
     * 查询最新成功事件
     *
     * @param appId appId
     * @param envId eventId
     * @return EventWithMarathonEntity
     */
    @Override
    public EventWithMarathonEntity selectTheLastSuccessEventWithMarathon(Long appId, Long envId) {
        return getMapper().selectTheLastSuccessEventWithMarathon(appId, envId);
    }

    /**
     * 更新状态
     *
     * @param appOperationEventEntity appOperationEventEntity
     */
    @Override
    public void updateStatus(AppOperationEventEntity appOperationEventEntity) {
        getMapper().updateByPrimaryKeySelective(appOperationEventEntity);
    }

    /**
     * 操作历史查询
     *
     * @param pageData pageData
     * @return pageData
     */
    @Override
    public PageData<AppOperateEventHistoryDto> getAppOperateEventHistory(AppOperateEventHistoryParam pageData) {
        List<AppOperateEventHistoryEntity> appOperateEventHistoryEntities = getMapper().getAppOperateEventHistory(pageData);
        if (CollectionUtils.isEmpty(appOperateEventHistoryEntities)) {
            pageData.setRows(new ArrayList<>());
            return pageData;
        }
        List<AppOperateEventHistoryDto> collect = appOperateEventHistoryEntities.stream()
                .map(appOperateEventHistoryEntity -> {
                    AppOperateEventHistoryDto appOperateEventHistoryDto = new AppOperateEventHistoryDto();
                    YyBeanUtils.copyProperties(appOperateEventHistoryEntity, appOperateEventHistoryDto);
                    appOperateEventHistoryDto.setEvent(appOperateEventHistoryEntity.getEvent().getDescription());
                    appOperateEventHistoryDto.setStatus(appOperateEventHistoryEntity.getStatus());
                    return appOperateEventHistoryDto;
                }).collect(Collectors.toList());
        pageData.setRows(collect);
        return pageData;
    }

    /**
     * 操作log
     *
     * @param eventId eventId
     * @return log
     */
    @Override
    public List<AppOperateEventLogDto> getAppOperateEventLog(Long eventId) {
        AppOperationEventEntity appOperationEventEntity = getMapper().selectByPrimaryKey(eventId);
        AppOperationEnum event = appOperationEventEntity.getEvent();
        List<AppOperateEventLogDto> result;
        if (event == AppOperationEnum.DEPLOY) {
            DeployEntity deployEntity = deployService.selectByEventId(eventId);
            List<DeployFlowEntity> deployFlowEntities = deployFlowService.selectByDeployId(deployEntity.getId());
            if (CollectionUtils.isEmpty(deployFlowEntities)) {
                return new ArrayList<>();
            }
            result = deployFlowEntities.stream()
                    .map(deployFlowEntity -> {
                        DeployFlowEnum flowType = deployFlowEntity.getFlowType();
                        String log = appOperationEventLog.get(eventId, flowType.getLogType());
                        return new AppOperateEventLogDto(flowType.getDescription(), log);
                    })
                    .collect(Collectors.toList());
        } else {
            AppOperationEventLogTypeEnum appOperationEventLogTypeEnum = event.getLogType();
            String log = appOperationEventLog.get(eventId, appOperationEventLogTypeEnum);
            result = Collections.singletonList(new AppOperateEventLogDto(event.getDescription(), log));
        }
        return result;
    }

    /**
     * 和marathon相关的操作事件
     *
     * @param pageData 参数
     * @return page data
     */
    @Override
    public PageData<AppOperateEventHistoryMarathonDto> getAppOperateEventHistoryMarathon(AppOperateEventHistoryMarathonParam pageData) {

        List<EventWithMarathonEntity> data = getMapper().page(pageData);

        if (CollectionUtils.isEmpty(data)) {
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
        List<RmsDockerImageDto> imageTags = rmsDockerImageApi.getDockerImageListByRegistryAndImageName(dockerHubUrl, dockerImageName)
                .ifNotSuccessThrowException()
                .getData();

        AppOperationEventEntity rollbackEntity = getRollbackEntity(pageData.getAppId(), pageData.getEnvId());
        Long templateEventId = null;
        if (rollbackEntity != null) {
            AppRollbackOperationEventPoJo appRollbackOperationEventPoJo = JSONObject.parseObject(rollbackEntity.getDetail(), AppRollbackOperationEventPoJo.class);
            templateEventId = appRollbackOperationEventPoJo.getTemplateEventId();
        }
        Long finalTemplateEventId = templateEventId;
        List<AppOperateEventHistoryMarathonDto> resultPageRow = data.stream()
                .map(eventWithMarathonEntity -> {
                    AppOperateEventHistoryMarathonDto dto = new AppOperateEventHistoryMarathonDto();
                    dto.setEvent(eventWithMarathonEntity.getEvent().getDescription());
                    dto.setEventId(eventWithMarathonEntity.getEventId());
                    dto.setStatus(eventWithMarathonEntity.getStatus().getDescription());
                    dto.setStatusCode(eventWithMarathonEntity.getStatus());
                    dto.setVersion(eventWithMarathonEntity.getMarathonDeploymentVersion());
                    String marathonConfig = eventWithMarathonEntity.getMarathonConfig();
                    App app = ModelUtils.GSON.fromJson(marathonConfig, App.class);
                    String image = app.getContainer().getDocker().getImage();
                    String[] split = image.split(":");
                    String imageTag = split[split.length - 1];
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
                    dto.setIsRollback(finalTemplateEventId != null && eventWithMarathonEntity.getEventId().compareTo(finalTemplateEventId) == 0);
                    return dto;
                })
                .collect(Collectors.toList());

        if (pageData.getPageNum() == 1 && !CollectionUtils.isEmpty(resultPageRow)) {
            resultPageRow.get(0).setIsTheLast(true);
        }
        pageData.setRows(resultPageRow);
        return pageData;
    }

    /**
     * 获取marathon配置
     *
     * @param eventId eventId
     * @return marathon配置
     */
    @Override
    public String getAppOperateEventHistoryMarathonDetail(Long eventId) {
        AppOperationEventMarathonEntity appOperationEventMarathonEntity = appOperationEventMarathonService.selectByEventId(eventId);
        if (appOperationEventMarathonEntity == null) {
            return "";
        }
        return appOperationEventMarathonEntity.getMarathonConfig();
    }

    @Override
    public AppEventOperateWithStatusDto getEventStatus(Long eventId) {
        AppOperationEventEntity appOperationEventEntity = selectById(eventId);
        AppEventOperateWithStatusDto dto = new AppEventOperateWithStatusDto();
        dto.setEventId(appOperationEventEntity.getId());
        dto.setEvent(appOperationEventEntity.getEvent());
        dto.setStatus(appOperationEventEntity.getStatus());
        return dto;
    }

    @Override
    public List<AppOperationEventEntity> selectByStatus(AppOperationEventStatusEnum status) {
        Example example = new Example(AppOperationEventEntity.class);
        example.createCriteria().andEqualTo("status", status);
        List<AppOperationEventEntity> entities = getMapper().selectByExample(example);
        return CollectionUtils.isEmpty(entities) ? new ArrayList<>() : entities;
    }

    @Override
    public List<AppRunningEventDto> getAppRunningEvent(Long appId) {
        Example example = new Example(AppOperationEventEntity.class);
        example.createCriteria().andEqualTo("status", AppOperationEventStatusEnum.PROCESS)
                .andEqualTo("appId", appId);
        List<AppOperationEventEntity> entities = getMapper().selectByExample(example);
        if (CollectionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(entity -> {
                    AppRunningEventDto dto = new AppRunningEventDto();
                    dto.setEnvId(entity.getEnvId());
                    dto.setEvent(entity.getEvent());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private AppOperationEventEntity getRollbackEntity(Long appId, Long envId) {
        return getMapper().selectRollbackEntity(appId, envId, AppOperationEventStatusEnum.PROCESS, AppOperationEnum.ROLLBACK);
    }
}
