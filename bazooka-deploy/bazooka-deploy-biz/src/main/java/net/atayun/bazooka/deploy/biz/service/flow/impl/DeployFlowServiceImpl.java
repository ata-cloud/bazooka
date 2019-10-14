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
package net.atayun.bazooka.deploy.biz.service.flow.impl;

import net.atayun.bazooka.combase.bean.StrategyNumBean;
import net.atayun.bazooka.combase.enums.deploy.DeployModeEnum;
import net.atayun.bazooka.combase.service.BatchService;
import net.atayun.bazooka.deploy.biz.dal.dao.flow.DeployFlowMapper;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;
import net.atayun.bazooka.deploy.biz.dto.flow.DeployFlowDto;
import net.atayun.bazooka.deploy.biz.dto.flow.DeployingFlowDto;
import net.atayun.bazooka.deploy.biz.enums.flow.DeployFlowEnum;
import net.atayun.bazooka.deploy.biz.enums.status.BasicStatusEnum;
import net.atayun.bazooka.deploy.biz.log.AppOperationEventLog;
import net.atayun.bazooka.deploy.biz.service.flow.DeployFlowService;
import net.atayun.bazooka.deploy.biz.service.flow.strategy.AbstractDeployFlowStrategy;
import com.youyu.common.exception.BizException;
import com.youyu.common.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.atayun.bazooka.combase.bean.SpringContextBean.getBean;
import static net.atayun.bazooka.deploy.biz.constants.DeployResultCodeConstants.UNKNOWN_FLOW;

/**
 * @author Ping
 * @date 2019-07-11
 */
@Service
public class DeployFlowServiceImpl
        extends AbstractService<Long, DeployFlowDto, DeployFlowEntity, DeployFlowMapper>
        implements DeployFlowService {

    @Autowired
    private BatchService batchService;

    /**
     * 初始化发布流
     *
     * @param deployId   deployId
     * @param deployMode deployMode
     *                   {@link DeployModeEnum}
     */
    @Override
    public void initFlows(Long deployId, DeployModeEnum deployMode) {
        AbstractDeployFlowStrategy beanInstance = StrategyNumBean.getBeanInstance(AbstractDeployFlowStrategy.class,
                deployMode.name());
        List<DeployFlowEntity> deployFlowEntities = beanInstance.initFlow(deployId);
        batchService.batchDispose(deployFlowEntities, DeployFlowMapper.class, "insertSelective");
    }

    /**
     * 更新流程状态
     *
     * @param deployId   发布Id
     * @param flowNumber 流程编号
     * @param status     流程状态
     */
    @Override
    public void updateFlowStatusByDeployIdAndFlowNumber(Long deployId, Integer flowNumber, BasicStatusEnum status) {
        DeployFlowEntity deployFlowEntity = selectByDeployIdAndFlowNumber(deployId, flowNumber);
        if (deployFlowEntity == null) {
            throw new BizException(UNKNOWN_FLOW, "发布流不存在");
        }
        DeployFlowEntity patch = new DeployFlowEntity();
        patch.setFlowStatus(status);
        patch.setId(deployFlowEntity.getId());
        getMapper().updateByPrimaryKeySelective(patch);
    }

    /**
     * 根据发布Id和流程号查询流程详细信息
     *
     * @param deployId   发布Id
     * @param flowNumber 流程编号
     * @return 流程详细信息
     */
    @Override
    public DeployFlowEntity selectByDeployIdAndFlowNumber(Long deployId, Integer flowNumber) {
        Example example = new Example(DeployFlowEntity.class);
        example.createCriteria()
                .andEqualTo("deployId", deployId)
                .andEqualTo("flowNumber", flowNumber);
        return getMapper().selectOneByExample(example);
    }

    /**
     * 通过Id查询
     *
     * @param deployFlowId deployFlowId
     * @return DeployFlowEntity
     */
    @Override
    public DeployFlowEntity selectById(Long deployFlowId) {
        return getMapper().selectByPrimaryKey(deployFlowId);
    }

    /**
     * 通过发布Id和流程类型查询
     *
     * @param deployId deployId
     * @param flowType deployFlowType
     * @return DeployFlowEntity
     */
    @Override
    public DeployFlowEntity selectByDeployIdAndFlowType(Long deployId, DeployFlowEnum flowType) {
        Example example = new Example(DeployFlowEntity.class);
        example.createCriteria()
                .andEqualTo("deployId", deployId)
                .andEqualTo("flowType", flowType);
        return getMapper().selectOneByExample(example);
    }

    /**
     * 更新状态
     *
     * @param deployFlowEntity deployFlowEntity
     */
    @Override
    public void updateFlowStatus(DeployFlowEntity deployFlowEntity) {
        getMapper().updateByPrimaryKeySelective(deployFlowEntity);
    }

    /**
     * 查询正在发布的流程
     *
     * @param eventId  eventId
     * @param deployId 发布Id
     * @return 正在发布的流程
     */
    @Override
    public List<DeployingFlowDto> getDeployingFlow(Long eventId, Long deployId) {
        List<DeployFlowEntity> deployFlowEntities = selectByDeployId(deployId);
        List<DeployFlowEntity> filter = new ArrayList<>();
        for (DeployFlowEntity deployFlowEntity : deployFlowEntities) {
            BasicStatusEnum flowStatus = deployFlowEntity.getFlowStatus();
            if (BasicStatusEnum.STAND_BY == flowStatus) {
                break;
            }
            filter.add(deployFlowEntity);
            if (BasicStatusEnum.FAILURE == flowStatus) {
                break;
            }
        }
        AppOperationEventLog appOperationEventLog = getBean(AppOperationEventLog.class);
        return filter.stream()
                .map(deployFlowEntity -> {
                    DeployingFlowDto deployingFlowDto = new DeployingFlowDto();
                    deployingFlowDto.setDeployFlowId(deployFlowEntity.getId());
                    deployingFlowDto.setDisplayName(deployFlowEntity.getFlowType().getDescription());
                    deployingFlowDto.setDeployId(deployFlowEntity.getDeployId());
                    BasicStatusEnum flowStatus = deployFlowEntity.getFlowStatus();
                    deployingFlowDto.setStatus(flowStatus);
                    if (flowStatus == BasicStatusEnum.SUCCESS || flowStatus == BasicStatusEnum.FAILURE) {
                        deployingFlowDto.setFinishDatetime(deployFlowEntity.getUpdateTime());
                    }
                    String log = appOperationEventLog.get(eventId, deployFlowEntity.getFlowType().getLogType());
                    deployingFlowDto.setLog(log);
                    return deployingFlowDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<DeployFlowEntity> selectByDeployId(Long deployId) {
        Example example = new Example(DeployFlowEntity.class);
        example.createCriteria()
                .andEqualTo("deployId", deployId);
        return getMapper().selectByExample(example);
    }
}
