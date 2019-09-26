package net.atayun.bazooka.delpoy2.strategy;

import net.atayun.bazooka.delpoy2.dal.entity.DeployCommand;

import java.util.List;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 上午10:51
 */
public abstract class DeployCommandStrategy {

    protected List<DeployActionStrategy> deployActionStrategyList;

    public abstract void execute(DeployCommand deployCommand);
}
