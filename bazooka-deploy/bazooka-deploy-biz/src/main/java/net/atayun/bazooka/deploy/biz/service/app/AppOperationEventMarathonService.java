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

import net.atayun.bazooka.deploy.biz.dal.entity.app.AppOperationEventMarathonEntity;

/**
 * @author Ping
 * @date 2019-07-26
 */
public interface AppOperationEventMarathonService {

    /**
     * insertEntity
     *
     * @param appOperationEventMarathonEntity appOperationEventMarathonEntity
     * @return id
     */
    Long insertEntity(AppOperationEventMarathonEntity appOperationEventMarathonEntity);

    /**
     * 通过marathon 信息查询
     *
     * @param marathonDeploymentId      marathonDeploymentId
     * @param marathonDeploymentVersion marathonDeploymentVersion
     * @return AppOperationEventMarathonEntity
     */
    AppOperationEventMarathonEntity selectByMarathonInfo(String marathonDeploymentId, String marathonDeploymentVersion);

    /**
     * 通过eventId查询
     *
     * @param eventId eventId
     * @return AppOperationEventMarathonEntity
     */
    AppOperationEventMarathonEntity selectByEventId(Long eventId);
}
