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
package net.atayun.bazooka.deploy.biz.dal.dao.app;

import net.atayun.bazooka.combase.enums.deploy.AppOperationEnum;
import net.atayun.bazooka.deploy.biz.dal.entity.app.AppOperateEventHistoryEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.app.AppOperationEventEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.app.EventWithMarathonEntity;
import net.atayun.bazooka.deploy.biz.enums.status.AppOperationEventStatusEnum;
import net.atayun.bazooka.deploy.biz.param.app.AppOperateEventHistoryMarathonParam;
import net.atayun.bazooka.deploy.biz.param.app.AppOperateEventHistoryParam;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Ping
 * @date 2019-07-25
 */
public interface AppOperationEventMapper extends YyMapper<AppOperationEventEntity> {

    /**
     * 查询服务最新成功事件
     *
     * @param appId appId
     * @param envId envId
     * @return EventWithMarathonEntity
     */
    @Select("select " +
            "d.id event_id, " +
            "d.app_id, " +
            "d.env_id, " +
            "d.event, " +
            "d.detail, " +
            "d.status, " +
            "dm.marathon_config," +
            "dm.marathon_deployment_version " +
            "from deploy_app_operation_event d " +
            "inner join deploy_app_operation_event_marathon dm on d.id = dm.event_id " +
            "where d.app_id = #{appId} and d.env_id = #{envId} and d.status = 'SUCCESS' " +
            "order by d.id desc " +
            "limit 1;")
    EventWithMarathonEntity selectTheLastSuccessEventWithMarathon(@Param("appId") Long appId, @Param("envId") Long envId);

    /**
     * appOperateEventHistoryParam
     *
     * @param pageData pageData
     * @return EventWithMarathonEntities
     */
    @Select("select " +
            "d.id event_id, " +
            "d.app_id, " +
            "d.env_id, " +
            "d.event, " +
            "d.detail, " +
            "d.status, " +
            "dm.marathon_config," +
            "dm.marathon_deployment_version " +
            "from deploy_app_operation_event d " +
            "inner join deploy_app_operation_event_marathon dm on d.id = dm.event_id " +
            "where d.app_id = #{pageData.appId} and d.env_id = #{pageData.envId} and d.event in ('DEPLOY', 'SCALE', 'ROLLBACK')" +
            "order by d.id desc ")
    List<EventWithMarathonEntity> page(@Param("pageData") AppOperateEventHistoryMarathonParam pageData);

    /**
     * 查询操作历史
     *
     * @param pageData 参数
     * @return AppOperateEventHistoryEntity
     */
    @Select("select " +
            "d.id event_id, " +
            "d.create_time operate_datetime, " +
            "d.create_author operator, " +
            "d.event, " +
            "d.detail, " +
            "d.status, " +
            "dd.remark " +
            "from deploy_app_operation_event d " +
            "inner join deploy_app_operation_event_detail dd on d.id = dd.event_id " +
            "where d.app_id = #{pageData.appId} and d.env_id = #{pageData.envId} " +
            "order by d.create_time desc ")
    List<AppOperateEventHistoryEntity> getAppOperateEventHistory(@Param("pageData") AppOperateEventHistoryParam pageData);

    /**
     * selectRollbackEntity
     *
     * @param appId  appId
     * @param envId  envId
     * @param status status
     * @param event  event
     * @return AppOperationEventEntity
     */
    @Select("select " +
            "* " +
            "from deploy_app_operation_event d " +
            "where d.app_id = #{appId} and d.env_id = #{envId} and d.status = #{status} and d.event = #{event} ")
    AppOperationEventEntity selectRollbackEntity(@Param("appId") Long appId,
                                                 @Param("envId") Long envId,
                                                 @Param("status") AppOperationEventStatusEnum status,
                                                 @Param("event") AppOperationEnum event);
}
