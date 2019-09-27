package net.atayun.bazooka.delpoy2.component.bridge.deploy;

import net.atayun.bazooka.base.annotation.BridgeAutowired;
import net.atayun.bazooka.delpoy2.component.listener.FlowEvent;
import net.atayun.bazooka.delpoy2.component.listener.FlowEvent4StartDeploy;
import net.atayun.bazooka.delpoy2.dal.entity.DeployCommand;
import net.atayun.bazooka.delpoy2.component.strategy.flow.DeployFlowStrategy;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;
import net.atayun.bazooka.deploy.biz.enums.flow.DispatchFlowSourceEnum;
import net.atayun.bazooka.deploy.biz.service.deploy.event.DispatchFlowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 下午1:33
 * 多流程执行策略
 */
public class DeployBridge4MulityFlow extends DeployBridge {

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @BridgeAutowired(bridgeValue = "1,2,3")
    public void setDeployActionStrategyList(List<DeployFlowStrategy> deployFlowStrategyList) {
        this.deployFlowStrategyList = deployFlowStrategyList;
    }

    @Override
    public void execute(DeployCommand deployCommand) {
        List<DeployFlowEntity> deployFlowEntities = initFlow(deployCommand.getAppEnvDeployConfigId());
        batchSave(deployFlowEntities);
        eventPublisher.publishEvent(new FlowEvent4StartDeploy(this, deployFlowEntities.get(0)));


    }

    private void batchSave(List<DeployFlowEntity> deployFlowEntities) {
        //TODO
    }


    private List<DeployFlowEntity> initFlow(Long appEnvDeployConfigId) {
        //TODO
        return null;
    }
}
