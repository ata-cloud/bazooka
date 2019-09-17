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

import net.atayun.bazooka.base.annotation.Bridge;
import net.atayun.bazooka.base.annotation.BridgeListAutowired;
import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.rms.biz.component.strategy.cluster.ClusterComponentStatusCalcStrategy;
import net.atayun.bazooka.rms.biz.dal.entity.RmsClusterConfigEntity;
import net.atayun.bazooka.rms.biz.enums.ClusterConfigTypeEnum;
import lombok.extern.slf4j.Slf4j;
import net.atayun.bazooka.rms.biz.enums.ClusterStatusEnum;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static net.atayun.bazooka.base.utils.EnumUtil.getEnum;
import static net.atayun.bazooka.base.utils.StringUtil.eq;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work dcos集群状态计算桥接
 */
@Slf4j
@Component
@StrategyNum(superClass = ClusterStatusCalcBridge.class, number = "0", describe = "计算Dcos集群状态")
@Bridge(associationClass = ClusterComponentStatusCalcStrategy.class, describe = "桥接ClusterComponentStatusCalcStrategy列表集合")
public class ClusterStatusCalcBridge4Dcos extends ClusterStatusCalcBridge {

    /**
     * 桥接注入
     *
     * @param clusterComponentStatusCalcStrategies
     */
    @BridgeListAutowired(bridgeValues = {"0", "1", "2", "3", "4", "5"})
    public void setClusterStatusCalcStrategies(List<ClusterComponentStatusCalcStrategy> clusterComponentStatusCalcStrategies) {
        this.clusterComponentStatusCalcStrategies = clusterComponentStatusCalcStrategies;
    }

    @Override
    protected String doCalcClusterStatus(List<RmsClusterConfigEntity> rmsClusterConfigEntities) {
        Map<String, List<RmsClusterConfigEntity>> map = rmsClusterConfigEntities.stream().collect(groupingBy(RmsClusterConfigEntity::getType));
        for (ClusterComponentStatusCalcStrategy clusterComponentStatusCalcStrategy : clusterComponentStatusCalcStrategies) {
            StrategyNum strategyNum = findAnnotation(clusterComponentStatusCalcStrategy.getClass(), StrategyNum.class);
            clusterComponentStatusCalcStrategy.calcComponentStatus(map.get(strategyNum.number()));
        }
        return doCalcDcosClusterStatus(rmsClusterConfigEntities, map);
    }

    /**
     * 计算dcos集群状态
     *
     * @param rmsClusterConfigEntities
     * @param map
     * @return
     */
    private String doCalcDcosClusterStatus(List<RmsClusterConfigEntity> rmsClusterConfigEntities, Map<String, List<RmsClusterConfigEntity>> map) {
        List<RmsClusterConfigEntity> dcosRmsClusterConfigEntities = rmsClusterConfigEntities.stream().filter(r -> getEnum(ClusterConfigTypeEnum.class, r.getType()).canRefreshDcosStatus()).collect(toList());
        boolean normalFlag = dcosRmsClusterConfigEntities.stream().allMatch(r -> eq(r.getStatus(), ClusterStatusEnum.NORMAL.getCode()));
        if (normalFlag) {
            return ClusterStatusEnum.NORMAL.getCode();
        }

        List<RmsClusterConfigEntity> dockerHubClusterConfigEntities = map.get(ClusterConfigTypeEnum.DOCKER_HUB.getCode());
        boolean dockerHubAbnormalFlag = dockerHubClusterConfigEntities.stream().anyMatch(r -> eq(r.getStatus(), ClusterStatusEnum.ABNORMAL.getCode()));
        if (dockerHubAbnormalFlag) {
            return ClusterStatusEnum.ABNORMAL.getCode();
        }

        List<RmsClusterConfigEntity> marathonClusterConfigEntities = map.get(ClusterConfigTypeEnum.MARATHON.getCode());
        boolean marathonAbnormalFlag = marathonClusterConfigEntities.stream().allMatch(r -> eq(r.getStatus(), ClusterStatusEnum.ABNORMAL.getCode()));
        if (marathonAbnormalFlag) {
            return ClusterStatusEnum.ABNORMAL.getCode();
        }

        List<RmsClusterConfigEntity> mlbClusterConfigEntities = map.get(ClusterConfigTypeEnum.MLB.getCode());
        boolean mlbAbnormalFlag = mlbClusterConfigEntities.stream().allMatch(r -> eq(r.getStatus(), ClusterStatusEnum.ABNORMAL.getCode()));
        if (mlbAbnormalFlag) {
            return ClusterStatusEnum.ABNORMAL.getCode();
        }

        return ClusterStatusEnum.USABLE.getCode();
    }

}
