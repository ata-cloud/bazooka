package net.atayun.bazooka.rms.biz.component.strategy.cluster;

import lombok.extern.slf4j.Slf4j;
import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.rms.biz.dal.entity.RmsClusterConfigEntity;
import net.atayun.bazooka.rms.biz.dal.entity.RmsClusterEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Ping
 */
@Slf4j
@Component
@StrategyNum(superClass = ClusterComponentRefreshStrategy.class, number = "2", describe = "Node")
public class ClusterComponentRefreshStrategy4Node implements ClusterComponentRefreshStrategy {

    @Override
    public void refreshClusterInfo(RmsClusterEntity rmsClusterEntity, List<RmsClusterConfigEntity> rmsClusterConfigEntities) {

    }
}
