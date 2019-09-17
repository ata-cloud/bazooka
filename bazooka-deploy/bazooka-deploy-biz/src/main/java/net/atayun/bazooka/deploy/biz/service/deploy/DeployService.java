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
package net.atayun.bazooka.deploy.biz.service.deploy;

import net.atayun.bazooka.deploy.biz.dal.entity.deploy.DeployEntity;
import net.atayun.bazooka.deploy.biz.dto.deploy.DeployCountsDto;
import net.atayun.bazooka.deploy.biz.dto.flow.DeployingConfigInfoDto;
import net.atayun.bazooka.deploy.biz.dto.flow.DeployingFlowResultDto;
import net.atayun.bazooka.deploy.biz.enums.TimeGranularityEnum;
import net.atayun.bazooka.deploy.biz.enums.status.BasicStatusEnum;
import net.atayun.bazooka.deploy.biz.service.app.event.AppDeployOperationEventPojo;

import java.util.List;

/**
 * @author Ping
 * @date 2019-07-11
 */
public interface DeployService {

    /**
     * 执行发布
     *
     * @param eventId                     eventId
     * @param appDeployOperationEventPojo appDeployOperationEventPojo
     * @return 发布Id
     */
    Long deployAction(Long eventId, AppDeployOperationEventPojo appDeployOperationEventPojo);

    /**
     * 更新状态
     *
     * @param deployId 发布Id
     * @param status   状态
     */
    void updateStatus(Long deployId, BasicStatusEnum status);

    /**
     * 通过ID查询Entity
     *
     * @param deployId deployId
     * @return
     */
    DeployEntity selectEntityById(Long deployId);

    /**
     * 通过事件ID查询
     *
     * @param eventId eventId
     * @return DeployEntity
     */
    DeployEntity selectByEventId(Long eventId);

    /**
     * 根据项目和时间汇总发布数
     *
     * @param projectId       projectId
     * @param timeGranularity timeGranularity
     * @return 汇总
     */
    List<DeployCountsDto> deployCountsByProject(Long projectId, TimeGranularityEnum timeGranularity);

    String getDeployFlowLog(Long deployId, Long deployFlowId);

    DeployingFlowResultDto getDeployingFlow(Long eventId);

    DeployingConfigInfoDto getDeployingConfigInfo(Long appId, Long envId);

    void updateStatusByEventId(Long eventId, BasicStatusEnum status);
}
