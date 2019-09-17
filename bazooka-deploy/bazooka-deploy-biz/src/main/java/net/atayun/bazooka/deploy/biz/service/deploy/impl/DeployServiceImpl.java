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
package net.atayun.bazooka.deploy.biz.service.deploy.impl;

import net.atayun.bazooka.base.enums.deploy.DeployModeEnum;
import net.atayun.bazooka.deploy.biz.dal.dao.deploy.DeployMapper;
import net.atayun.bazooka.deploy.biz.dal.entity.deploy.DeployCountsEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.deploy.DeployEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;
import net.atayun.bazooka.deploy.biz.dto.deploy.DeployCountsDto;
import net.atayun.bazooka.deploy.biz.dto.deploy.DeployDto;
import net.atayun.bazooka.deploy.biz.dto.flow.DeployingConfigInfoDto;
import net.atayun.bazooka.deploy.biz.dto.flow.DeployingFlowDto;
import net.atayun.bazooka.deploy.biz.dto.flow.DeployingFlowResultDto;
import net.atayun.bazooka.deploy.biz.enums.TimeGranularityEnum;
import net.atayun.bazooka.deploy.biz.enums.flow.DispatchFlowSourceEnum;
import net.atayun.bazooka.deploy.biz.enums.status.BasicStatusEnum;
import net.atayun.bazooka.deploy.biz.log.AppOperationEventLog;
import net.atayun.bazooka.deploy.biz.service.app.event.AppDeployOperationEventPojo;
import net.atayun.bazooka.deploy.biz.service.deploy.DeployService;
import net.atayun.bazooka.deploy.biz.service.deploy.event.DispatchFlowEvent;
import net.atayun.bazooka.deploy.biz.service.deploy.event.DispatchFlowEventPojo;
import net.atayun.bazooka.deploy.biz.service.flow.DeployFlowService;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.dto.AppInfoDto;
import net.atayun.bazooka.pms.api.feign.AppApi;
import com.youyu.common.exception.BizException;
import com.youyu.common.service.AbstractService;
import com.youyu.common.utils.YyBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;
import static net.atayun.bazooka.deploy.biz.constants.DeployResultCodeConstants.*;

/**
 * @author Ping
 * @date 2019-07-11
 */
@Service
public class DeployServiceImpl
        extends AbstractService<Long, DeployDto, DeployEntity, DeployMapper>
        implements DeployService {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DeployFlowService deployFlowService;

    @Autowired
    private AppApi appApi;

    /**
     * 执行发布
     *
     * @param eventId                     eventId
     * @param appDeployOperationEventPojo appDeployOperationEventPojo
     * @return 发布Id
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long deployAction(Long eventId, AppDeployOperationEventPojo appDeployOperationEventPojo) {
        //调用pms服务 获取发布配置详情
        AppDeployConfigDto deployConfig = appApi.getAppDeployConfigInfoById(appDeployOperationEventPojo.getDeployConfigId())
                .ifNotSuccessThrowException()
                .getData();
        if (deployConfig.getDeployMode() == DeployModeEnum.BUILD) {
            Assert.hasText(appDeployOperationEventPojo.getBranch(), "构建发布必须要选择分支");
        } else if (deployConfig.getDeployMode() == DeployModeEnum.DOCKER_IMAGE) {
            Assert.hasText(appDeployOperationEventPojo.getDockerImageTag(), "镜像发布必须要选择镜像版本");
        }

        AppInfoDto appInfo = appApi.getAppInfoById(deployConfig.getAppId())
                .ifNotSuccessThrowException()
                .getData();

        DeployEntity deployEntity = isDeploying(appInfo.getId(), deployConfig.getEnvId());
        if (deployEntity != null) {
            throw new BizException(APP_IS_DEPLOYING_ERR_CODE, "服务正在发布");
        }

        deployEntity = new DeployEntity();
        deployEntity.setEventId(eventId);
        deployEntity.setAppId(appInfo.getId());
        deployEntity.setAppName(appInfo.getAppName());
        deployEntity.setProjectId(appInfo.getProjectId());
        deployEntity.setEnvId(deployConfig.getEnvId());
        deployEntity.setDeployConfigId(deployConfig.getId());
        deployEntity.setBranch(appDeployOperationEventPojo.getBranch());
        deployEntity.setDockerImageTag(appDeployOperationEventPojo.getDockerImageTag());
        deployEntity.setStatus(BasicStatusEnum.PROCESS);
        getMapper().insertSelective(deployEntity);

        Long deployId = deployEntity.getId();

        deployFlowService.initFlows(deployId, deployConfig.getDeployMode());

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                DispatchFlowEventPojo eventPojo = new DispatchFlowEventPojo(deployId, 0);
                applicationContext.publishEvent(new DispatchFlowEvent(DispatchFlowSourceEnum.DEPLOY_ACTION, eventPojo));
            }
        });

        return deployId;
    }

    /**
     * 更新状态
     *
     * @param deployId 发布Id
     * @param status   状态
     */
    @Override
    public void updateStatus(Long deployId, BasicStatusEnum status) {
        DeployEntity deployEntity = new DeployEntity();
        deployEntity.setId(deployId);
        deployEntity.setStatus(status);
        getMapper().updateByPrimaryKeySelective(deployEntity);
    }

    /**
     * 通过ID查询Entity
     *
     * @param deployId deployId
     * @return DeployEntity
     */
    @Override
    public DeployEntity selectEntityById(Long deployId) {
        DeployEntity deployEntity = getMapper().selectByPrimaryKey(deployId);
        if (deployEntity == null) {
            throw new BizException(MISS_DEPLOY_ENTITY_ERR_CODE, "没有该发布记录");
        }
        return deployEntity;
    }

    /**
     * 查询服务是否在发布
     *
     * @param appId app id
     * @param envId env id
     * @return deploy
     */
    public DeployEntity isDeploying(Long appId, Long envId) {
        Example example = new Example(DeployEntity.class);
        example.createCriteria()
                .andEqualTo("appId", appId)
                .andEqualTo("envId", envId)
                .andEqualTo("status", BasicStatusEnum.PROCESS);
        List<DeployEntity> deployEntities = getMapper().selectByExample(example);
        if (CollectionUtils.isEmpty(deployEntities)) {
            return null;
        }
        if (deployEntities.size() > 1) {
            throw new BizException(MULTI_DEPLOYING_ENTITY_ERR_CODE, "发布记录异常");
        }
        return deployEntities.get(0);
    }

    /**
     * 通过事件ID查询
     *
     * @param eventId eventId
     * @return DeployEntity
     */
    @Override
    public DeployEntity selectByEventId(Long eventId) {
        Example example = new Example(DeployEntity.class);
        example.createCriteria()
                .andEqualTo("eventId", eventId);
        return getMapper().selectOneByExample(example);
    }

    /**
     * 根据项目和时间汇总发布数
     *
     * @param projectId       projectId
     * @param timeGranularity timeGranularity
     * @return 汇总
     */
    @Override
    public List<DeployCountsDto> deployCountsByProject(Long projectId, TimeGranularityEnum timeGranularity) {
        LocalDateTime leftDatetime = timeGranularity.getLeftDatetime();
        List<DeployCountsEntity> deployCountsEntities = getMapper().deployCountsByProject(projectId, leftDatetime);
        if (CollectionUtils.isEmpty(deployCountsEntities)) {
            return new ArrayList<>();
        }
        return deployCountsEntities.stream()
                .map(deployCountsEntity -> {
                    DeployCountsDto deployCountsDto = new DeployCountsDto();
                    YyBeanUtils.copyProperties(deployCountsEntity, deployCountsDto);
                    return deployCountsDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public String getDeployFlowLog(Long deployId, Long deployFlowId) {
        DeployEntity deployEntity = selectEntityById(deployId);
        if (deployEntity == null) {
            throw new BizException(ILLEGAL_DEPLOY_ID_ERR_CODE, "无效deployId");
        }
        DeployFlowEntity deployFlowEntity = deployFlowService.selectById(deployFlowId);
        if (deployFlowEntity == null) {
            throw new BizException(ILLEGAL_DEPLOY_FLOW_ID_ERR_CODE, "无效deployFlowId");
        }
        return getBean(AppOperationEventLog.class).get(deployEntity.getEventId(), deployFlowEntity.getFlowType().getLogType());
    }

    @Override
    public DeployingFlowResultDto getDeployingFlow(Long eventId) {
        DeployEntity deployEntity = selectByEventId(eventId);
        if (deployEntity == null) {
            throw new BizException(ILLEGAL_EVENT_ID_ERR_CODE, "无效事件ID");
        }
        List<DeployingFlowDto> deployingFlow = deployFlowService.getDeployingFlow(deployEntity.getEventId(), deployEntity.getId());
        DeployingFlowResultDto resultDto = new DeployingFlowResultDto();
        resultDto.setStatus(deployEntity.getStatus());
        resultDto.setFlows(deployingFlow);
        return resultDto;
    }

    @Override
    public DeployingConfigInfoDto getDeployingConfigInfo(Long appId, Long envId) {
        DeployEntity deployEntity = isDeploying(appId, envId);
        if (deployEntity == null) {
            return null;
        }
        DeployingConfigInfoDto deployingConfigInfoDto = new DeployingConfigInfoDto();
        deployingConfigInfoDto.setEventId(deployEntity.getEventId());
        deployingConfigInfoDto.setDeployConfigId(deployEntity.getDeployConfigId());
        deployingConfigInfoDto.setBranch(deployEntity.getBranch());
        deployingConfigInfoDto.setDockerImageTag(deployEntity.getDockerImageTag());
        AppDeployConfigDto data = appApi.getAppDeployConfigInfoById(deployEntity.getDeployConfigId())
                .ifNotSuccessThrowException()
                .getData();
        DeployModeEnum deployMode = data.getDeployMode();
        deployingConfigInfoDto.setDeployMode(deployMode);
        return deployingConfigInfoDto;
    }

    @Override
    public void updateStatusByEventId(Long eventId, BasicStatusEnum status) {
        DeployEntity deployEntity = selectByEventId(eventId);
        if (deployEntity == null) {
            return;
        }
        updateStatus(deployEntity.getId(), status);
    }

    private DeployEntity selectLastOne(Long appId, Long envId) {
        return getMapper().selectLastOne(appId, envId);
    }
}
