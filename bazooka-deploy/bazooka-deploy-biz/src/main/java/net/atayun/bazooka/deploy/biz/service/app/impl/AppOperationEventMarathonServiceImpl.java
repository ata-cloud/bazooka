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

import net.atayun.bazooka.deploy.biz.dal.dao.app.AppOperationEventMarathonMapper;
import net.atayun.bazooka.deploy.biz.dal.entity.app.AppOperationEventMarathonEntity;
import net.atayun.bazooka.deploy.biz.dto.app.AppOperationEventMarathonDto;
import net.atayun.bazooka.deploy.biz.service.app.AppOperationEventMarathonService;
import com.youyu.common.service.AbstractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * @author Ping
 * @date 2019-07-26
 */
@Slf4j
@Service
public class AppOperationEventMarathonServiceImpl
        extends AbstractService<Long, AppOperationEventMarathonDto, AppOperationEventMarathonEntity, AppOperationEventMarathonMapper>
        implements AppOperationEventMarathonService {

    /**
     * insertEntity
     *
     * @param appOperationEventMarathonEntity appOperationEventMarathonEntity
     * @return id
     */
    @Override
    public Long insertEntity(AppOperationEventMarathonEntity appOperationEventMarathonEntity) {
        getMapper().insertSelective(appOperationEventMarathonEntity);
        return appOperationEventMarathonEntity.getId();
    }

    /**
     * 通过marathon 信息查询
     *
     * @param marathonDeploymentId      marathonDeploymentId
     * @param marathonDeploymentVersion marathonDeploymentVersion
     * @return AppOperationEventMarathonEntity
     */
    @Override
    public AppOperationEventMarathonEntity selectByMarathonInfo(String marathonDeploymentId, String marathonDeploymentVersion) {
        Example example = new Example(AppOperationEventMarathonEntity.class);
        example.createCriteria()
                .andEqualTo("marathonDeploymentId", marathonDeploymentId)
                .andEqualTo("marathonDeploymentVersion", marathonDeploymentVersion);
        return getMapper().selectOneByExample(example);
    }

    @Override
    public AppOperationEventMarathonEntity selectByServiceIdAndVersion(String marathonServiceId, String marathonDeploymentVersion) {
        Example example = new Example(AppOperationEventMarathonEntity.class);
        example.createCriteria()
                .andEqualTo("marathonServiceId", marathonServiceId)
                .andEqualTo("marathonDeploymentVersion", marathonDeploymentVersion);
        return getMapper().selectOneByExample(example);
    }

    /**
     * 通过eventId查询
     *
     * @param eventId eventId
     * @return AppOperationEventMarathonEntity
     */
    @Override
    public AppOperationEventMarathonEntity selectByEventId(Long eventId) {
        Example example = new Example(AppOperationEventMarathonEntity.class);
        example.createCriteria()
                .andEqualTo("eventId", eventId);
        return getMapper().selectOneByExample(example);
    }
}
