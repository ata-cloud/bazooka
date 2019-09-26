package net.atayun.bazooka.delpoy2.strategy;

import net.atayun.bazooka.delpoy2.dal.entity.DeployCommand;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 上午10:51
 */
public interface DeployActionStrategy {



   void action(DeployCommand deployCommand);
}
