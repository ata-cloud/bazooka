package net.atayun.bazooka.delpoy2.strategy;

import net.atayun.bazooka.base.annotation.BridgeAutowired;
import net.atayun.bazooka.delpoy2.dal.entity.DeployCommand;

import java.util.List;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 下午1:33
 */
public class DeployCommandStrategy4ComplierDeploy extends DeployCommandStrategy {


    @BridgeAutowired(bridgeValue = "1,2,3")
    public void setDeployActionStrategyList(List<DeployActionStrategy> deployActionStrategyList) {
        this.deployActionStrategyList = deployActionStrategyList;
    }

    @Override
    public void execute(DeployCommand deployCommand) {
        for (DeployActionStrategy deployActionStrategy : deployActionStrategyList) {
            deployActionStrategy.action(deployCommand);
        }
    }
}
