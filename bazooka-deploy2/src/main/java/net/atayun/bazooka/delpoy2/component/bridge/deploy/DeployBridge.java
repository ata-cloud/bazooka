package net.atayun.bazooka.delpoy2.component.bridge.deploy;

import net.atayun.bazooka.delpoy2.dal.entity.DeployCommand;
import net.atayun.bazooka.delpoy2.component.strategy.flow.DeployFlowStrategy;

import java.util.List;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 上午10:51
 */
public abstract class DeployBridge {

    protected List<DeployFlowStrategy> deployFlowStrategyList;

    public abstract void execute(DeployCommand deployCommand);
}
