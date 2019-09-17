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
package net.atayun.bazooka.deploy.biz.service.app;

import net.atayun.bazooka.base.enums.status.FinishStatusEnum;
import net.atayun.bazooka.deploy.api.dto.AppRunningEventDto;
import net.atayun.bazooka.deploy.api.param.AppOperationEventParam;
import net.atayun.bazooka.deploy.biz.dal.entity.app.AppOperationEventEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.app.EventWithMarathonEntity;
import net.atayun.bazooka.deploy.biz.dto.app.AppEventOperateWithStatusDto;
import net.atayun.bazooka.deploy.biz.dto.app.AppOperateEventHistoryDto;
import net.atayun.bazooka.deploy.biz.dto.app.AppOperateEventHistoryMarathonDto;
import net.atayun.bazooka.deploy.biz.dto.app.AppOperateEventLogDto;
import net.atayun.bazooka.deploy.biz.enums.status.AppOperationEventStatusEnum;
import net.atayun.bazooka.deploy.biz.param.app.AppOperateEventHistoryMarathonParam;
import net.atayun.bazooka.deploy.biz.param.app.AppOperateEventHistoryParam;
import com.youyu.common.api.PageData;

import java.util.List;

/**
 * @author Ping
 * @date 2019-07-25
 */
public interface AppOperationEventService {

    /**
     * 事件操作
     *
     * @param appOperationEventParam 参数
     * @return 事件ID
     */
    Long operationEvent(AppOperationEventParam appOperationEventParam);

    /**
     * marathon callback
     *
     * @param marathonDeploymentId      marathonDeploymentId
     * @param marathonDeploymentVersion marathonDeploymentVersion
     * @param status                    status
     */
    void marathonCallback(String marathonDeploymentId, String marathonDeploymentVersion, FinishStatusEnum status);

    /**
     * id 查询
     *
     * @param eventId eventId
     * @return AppOperationEventEntity
     */
    AppOperationEventEntity selectById(Long eventId);

    /**
     * 查询最新成功事件
     *
     * @param appId appId
     * @param envId eventId
     * @return EventWithMarathonEntity
     */
    EventWithMarathonEntity selectTheLastSuccessEventWithMarathon(Long appId, Long envId);

    /**
     * 更新状态
     *
     * @param appOperationEventEntity appOperationEventEntity
     */
    void updateStatus(AppOperationEventEntity appOperationEventEntity);

    /**
     * 操作历史查询
     *
     * @param pageParam pageParam
     * @return pageData
     */
    PageData<AppOperateEventHistoryDto> getAppOperateEventHistory(AppOperateEventHistoryParam pageParam);

    /**
     * 操作log
     *
     * @param eventId eventId
     * @return log
     */
    List<AppOperateEventLogDto> getAppOperateEventLog(Long eventId);

    /**
     * 和marathon相关的操作事件
     *
     * @param pageParam 参数
     * @return page data
     */
    PageData<AppOperateEventHistoryMarathonDto> getAppOperateEventHistoryMarathon(AppOperateEventHistoryMarathonParam pageParam);

    /**
     * 获取marathon配置
     *
     * @param eventId eventId
     * @return marathon配置
     */
    String getAppOperateEventHistoryMarathonDetail(Long eventId);

    AppEventOperateWithStatusDto getEventStatus(Long eventId);

    List<AppOperationEventEntity> selectByStatus(AppOperationEventStatusEnum status);

    List<AppRunningEventDto> getAppRunningEvent(Long appId);
}
