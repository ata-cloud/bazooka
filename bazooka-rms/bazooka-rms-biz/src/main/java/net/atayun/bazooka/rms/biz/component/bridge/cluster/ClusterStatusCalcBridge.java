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
package net.atayun.bazooka.rms.biz.component.bridge.cluster;

import net.atayun.bazooka.rms.biz.component.strategy.cluster.ClusterComponentStatusCalcStrategy;
import net.atayun.bazooka.rms.biz.dal.entity.RmsClusterConfigEntity;
import net.atayun.bazooka.rms.biz.enums.ClusterStatusEnum;

import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 集群状态计算桥接
 */
public abstract class ClusterStatusCalcBridge {

    /**
     * 注入集群组件计算策略列表
     */
    protected List<ClusterComponentStatusCalcStrategy> clusterComponentStatusCalcStrategies;

    /**
     * 计算集群状态
     *
     * @param rmsClusterConfigEntities
     * @return
     */
    public String calcClusterStatus(List<RmsClusterConfigEntity> rmsClusterConfigEntities) {
        if (isEmpty(rmsClusterConfigEntities)) {
            return ClusterStatusEnum.ABNORMAL.getCode();
        }

        if (isEmpty(clusterComponentStatusCalcStrategies)) {
            return ClusterStatusEnum.ABNORMAL.getCode();
        }
        return doCalcClusterStatus(rmsClusterConfigEntities);
    }

    /**
     * 执行计算集群状态
     *
     * @param rmsClusterConfigEntities
     * @return
     */
    protected abstract String doCalcClusterStatus(List<RmsClusterConfigEntity> rmsClusterConfigEntities);

}
