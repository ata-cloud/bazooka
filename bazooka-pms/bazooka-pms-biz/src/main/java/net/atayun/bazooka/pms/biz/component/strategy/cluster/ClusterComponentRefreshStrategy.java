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
package net.atayun.bazooka.pms.biz.component.strategy.cluster;

import net.atayun.bazooka.combase.dcos.dto.MesosTaskDto;
import net.atayun.bazooka.combase.dcos.dto.TaskDto;
import net.atayun.bazooka.pms.biz.dal.entity.RmsClusterConfigEntity;
import net.atayun.bazooka.pms.biz.dal.entity.RmsClusterEntity;

import java.util.List;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 集群组件刷新策略
 */
public interface ClusterComponentRefreshStrategy {

    /**
     * 刷新集群组件
     *
     * @param rmsClusterEntity
     * @param rmsClusterConfigEntities
     */
    void refreshClusterInfo(RmsClusterEntity rmsClusterEntity, List<RmsClusterConfigEntity> rmsClusterConfigEntities);

    /**
     * 获取集群容器task信息
     *
     * @param rmsClusterConfigEntity
     * @return
     */
    default TaskDto getTask(RmsClusterConfigEntity rmsClusterConfigEntity){
        return null;
    }

    /**
     * 获取集群容器Mesos task信息
     *
     * @param rmsClusterConfigEntity
     * @return
     */
    default MesosTaskDto getMesosTask(RmsClusterConfigEntity rmsClusterConfigEntity){
        return null;
    }
}
