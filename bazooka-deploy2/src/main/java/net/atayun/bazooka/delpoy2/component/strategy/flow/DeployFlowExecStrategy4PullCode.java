package net.atayun.bazooka.delpoy2.component.strategy.flow;

import net.atayun.bazooka.combase.annotation.StrategyNum;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;
import net.atayun.bazooka.deploy.biz.enums.flow.DeployFlowEnum;
import org.springframework.stereotype.Component;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 下午1:52
 */


@Component
@StrategyNum(superClass = DeployFlowExecStrategy.class, number = DeployFlowEnum.FLOW_PULL_CODE, describe = "拉取Git代码步骤")
public class DeployFlowExecStrategy4PullCode implements DeployFlowExecStrategy {
    @Override
    public void execute(DeployFlowEntity firstDeployFlow) {

    }
}
