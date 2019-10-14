package net.atayun.bazooka.delpoy2.component.strategy.deploy;

import net.atayun.bazooka.combase.annotation.Bridge;
import net.atayun.bazooka.combase.annotation.StrategyNum;
import net.atayun.bazooka.delpoy2.component.strategy.flow.DeployFlowExecStrategy;
import net.atayun.bazooka.delpoy2.dal.entity.DeployEntity;
import net.atayun.bazooka.delpoy2.dto.DeployCommandReqDto;
import org.springframework.stereotype.Component;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 上午10:58
 * 单流程执行策略
 */
@Component
@StrategyNum(superClass = DeployCreateAndExecuteStrategy.class, number = "0", describe = "停止发布")
@Bridge(associationClass = DeployFlowExecStrategy.class)
public class DeployCreateAndExecuteStrategy4Stop extends DeployCreateAndExecuteStrategy {

    @Override
    protected DeployEntity create(DeployCommandReqDto deployCommandReqDto) {
        return null;
    }

    @Override
    protected DeployEntity buildDeployCommand(DeployCommandReqDto deployCommand) {
        return null;
    }
}
