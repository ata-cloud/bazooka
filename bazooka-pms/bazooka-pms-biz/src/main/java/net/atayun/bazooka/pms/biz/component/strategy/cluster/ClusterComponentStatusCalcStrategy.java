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

import net.atayun.bazooka.pms.biz.dal.entity.RmsClusterConfigEntity;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 集群状态计算策略
 */
public interface ClusterComponentStatusCalcStrategy {

    /**
     * 计算组件状态
     *
     * @param rmsClusterConfigEntities
     */
    default void calcComponentStatus(List<RmsClusterConfigEntity> rmsClusterConfigEntities) {
        if (isEmpty(rmsClusterConfigEntities)) {
            return;
        }

        for (RmsClusterConfigEntity rmsClusterConfigEntity : rmsClusterConfigEntities) {
            doCalcComponentStatus(rmsClusterConfigEntity);
        }
    }

    /**
     * 执行计算组件状态
     *
     * @param rmsClusterConfigEntity
     */
    void doCalcComponentStatus(RmsClusterConfigEntity rmsClusterConfigEntity);
}
