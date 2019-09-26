package net.atayun.bazooka.delpoy2.strategy;

import net.atayun.bazooka.base.annotation.Bridge;
import net.atayun.bazooka.base.annotation.BridgeAutowired;
import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.delpoy2.dal.entity.DeployCommand;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 上午10:58
 */
@Component
@StrategyNum(superClass = DeployCommandStrategy.class, number = "0", describe = "xxxxx")
@Bridge(associationClass = DeployActionStrategy.class)
public class DeployCommandStrategy4Restart extends DeployCommandStrategy {

    @BridgeAutowired(bridgeValue = "1,2,3")
    public void setDeployActionStrategyList(List<DeployActionStrategy> deployActionStrategyList) {
        this.deployActionStrategyList = deployActionStrategyList;
    }

    @Override
    public void execute(DeployCommand deployCommand) {
        // TODO: 2019/9/25
        for (DeployActionStrategy deployActionStrategy : deployActionStrategyList) {
            deployActionStrategy.action();
        }
        // TODO: 2019/9/25
    }
}
