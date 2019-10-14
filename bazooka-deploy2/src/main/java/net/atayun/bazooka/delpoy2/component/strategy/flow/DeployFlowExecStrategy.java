package net.atayun.bazooka.delpoy2.component.strategy.flow;

import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 上午10:51
 */
public interface DeployFlowExecStrategy {


    /**
     * 执行
     *
     * @param deployFlowEntity
     */
    void execute(DeployFlowEntity deployFlowEntity);
}
