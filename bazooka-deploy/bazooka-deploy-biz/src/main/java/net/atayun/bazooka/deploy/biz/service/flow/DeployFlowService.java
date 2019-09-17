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
package net.atayun.bazooka.deploy.biz.service.flow;

import net.atayun.bazooka.base.enums.deploy.DeployModeEnum;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;
import net.atayun.bazooka.deploy.biz.dto.flow.DeployingFlowDto;
import net.atayun.bazooka.deploy.biz.enums.flow.DeployFlowEnum;
import net.atayun.bazooka.deploy.biz.enums.status.BasicStatusEnum;

import java.util.List;

/**
 * @author Ping
 * @date 2019-07-11
 */
public interface DeployFlowService {

    /**
     * 初始化发布流
     *
     * @param deployId   发布Id
     * @param deployMode 发布模式
     *                   {@link DeployModeEnum}
     */
    void initFlows(Long deployId, DeployModeEnum deployMode);

    /**
     * 更新流程状态
     *
     * @param deployId   发布Id
     * @param flowNumber 流程编号
     * @param status     流程状态
     */
    void updateFlowStatusByDeployIdAndFlowNumber(Long deployId, Integer flowNumber, BasicStatusEnum status);

    /**
     * 根据发布Id和流程号查询流程详细信息
     *
     * @param deployId   发布Id
     * @param flowNumber 流程编号
     * @return 流程详细信息
     */
    DeployFlowEntity selectByDeployIdAndFlowNumber(Long deployId, Integer flowNumber);

    /**
     * 通过Id查询
     *
     * @param deployFlowId deployFlowId
     * @return DeployFlowEntity
     */
    DeployFlowEntity selectById(Long deployFlowId);

    /**
     * 通过发布Id和流程类型查询
     *
     * @param deployId       deployId
     * @param deployFlowType deployFlowType
     * @return DeployFlowEntity
     */
    DeployFlowEntity selectByDeployIdAndFlowType(Long deployId, DeployFlowEnum deployFlowType);

    /**
     * 更新状态
     *
     * @param deployFlowEntity deployFlowEntity
     */
    void updateFlowStatus(DeployFlowEntity deployFlowEntity);

    /**
     * 查询正在发布的流程
     *
     * @param eventId  eventId
     * @param deployId 发布Id
     * @return 正在发布的流程
     */
    List<DeployingFlowDto> getDeployingFlow(Long eventId, Long deployId);

    List<DeployFlowEntity> selectByDeployId(Long deployId);
}
