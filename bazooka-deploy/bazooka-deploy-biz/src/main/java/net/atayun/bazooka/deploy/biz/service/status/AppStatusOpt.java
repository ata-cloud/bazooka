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
package net.atayun.bazooka.deploy.biz.service.status;

import net.atayun.bazooka.combase.bean.SpringContextBean;
import net.atayun.bazooka.combase.enums.deploy.AppOperationEnum;
import net.atayun.bazooka.deploy.biz.dal.dao.app.AppOperationEventMapper;
import net.atayun.bazooka.deploy.biz.dal.entity.app.AppOperationEventEntity;
import net.atayun.bazooka.pms.api.feign.AppApi;

import static net.atayun.bazooka.combase.bean.SpringContextBean.getBean;

/**
 * @author Ping
 * @date 2019-08-16
 */
public interface AppStatusOpt {

    /**
     * 更新app的状态
     *
     * @param appId appId
     * @param envId envId
     * @param isOpt isOpt
     * @param event event
     */
    default void updateAppStatus(Long appId, Long envId, boolean isOpt, AppOperationEnum event) {
        if (event.getAppRunStatus()) {
            getBean(AppApi.class).updateAppDeployStatus(appId, envId, isOpt, event);
        }
    }

    /**
     * 更新app的状态
     *
     * @param eventId eventId
     * @param isOpt   isOpt
     */
    default void updateAppStatus(Long eventId, boolean isOpt) {
        AppOperationEventEntity appOperationEventEntity = SpringContextBean.getBean(AppOperationEventMapper.class).selectByPrimaryKey(eventId);
        AppOperationEnum event = appOperationEventEntity.getEvent();
        updateAppStatus(appOperationEventEntity.getAppId(), appOperationEventEntity.getEnvId(), isOpt, event);
    }
}
