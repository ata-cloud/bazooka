package net.atayun.bazooka.delpoy2.component.bridge.deploy;

import net.atayun.bazooka.base.annotation.Bridge;
import net.atayun.bazooka.base.annotation.BridgeAutowired;
import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.delpoy2.dal.entity.DeployCommand;
import net.atayun.bazooka.delpoy2.component.strategy.flow.DeployFlowStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 上午10:58
 * 单流程执行策略
 */
@Component
@StrategyNum(superClass = DeployBridge.class, number = "0", describe = "xxxxx")
@Bridge(associationClass = DeployFlowStrategy.class)
public class DeployBridge4SingleFlow extends DeployBridge {

    @BridgeAutowired(bridgeValue = "1")
    public void setDeployActionStrategyList(List<DeployFlowStrategy> deployFlowStrategyList) {
        this.deployFlowStrategyList = deployFlowStrategyList;
    }

    @Override
    public void execute(DeployCommand deployCommand) {
        // TODO: 2019/9/25
        DeployFlowStrategy deployFlowStrategy =  singleActionCheckAndGet(deployFlowStrategyList);
        deployFlowStrategy.action(deployCommand);
        // TODO: 2019/9/25
    }

    private DeployFlowStrategy singleActionCheckAndGet(List<DeployFlowStrategy> deployFlowStrategyList) {
        //TODO
        return null;
    }

}
