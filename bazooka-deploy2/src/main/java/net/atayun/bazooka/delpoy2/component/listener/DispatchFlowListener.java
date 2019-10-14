package net.atayun.bazooka.delpoy2.component.listener;

import lombok.extern.slf4j.Slf4j;
import net.atayun.bazooka.combase.bean.StrategyNumBean;
import net.atayun.bazooka.delpoy2.component.strategy.flow.DeployFlowExecStrategy;
import net.atayun.bazooka.delpoy2.dal.entity.DeployEntity;
import net.atayun.bazooka.delpoy2.dal.dao.DeployCommandMapper;
import net.atayun.bazooka.deploy.biz.dal.dao.flow.DeployFlowMapper;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/26 下午4:37
 */
@Configuration
@Slf4j
public class DispatchFlowListener {
    @Autowired
    ApplicationEventPublisher eventPublisher;
    @Autowired
    private DeployFlowMapper deployFlowMapper;
    @Autowired
    private DeployCommandMapper deployCommandMapper;


    @EventListener
    public void handleFlowEvent4StartDeploy(FlowEvent4StartDeploy flowEvent4StartDeploy) {
        DeployFlowEntity firstDeployFlow = flowEvent4StartDeploy.getDeployFlowEntity();
        excDeployFlow(firstDeployFlow);

    }

    @EventListener
    public void handleFlowEvent4FlowSuccess(FlowEvent4FlowSuccess flowSuccess) {
        DeployFlowEntity currentDeployFlow = flowSuccess.getDeployFlowEntity();
        currentDeployFlow.callSucc();
        deployFlowMapper.updateFlowStatus(currentDeployFlow);
        DeployFlowEntity nextDeployFlow = getNextDeployFlow(flowSuccess.getDeployFlowEntity());
        if (!hasNextFlow(nextDeployFlow)) {
            eventPublisher.publishEvent(new FlowEvent4FlowAllSuccess(this, currentDeployFlow));
            return;
        }
        excDeployFlow(nextDeployFlow);
    }

    @EventListener
    public void handleFlowEvent4FlowFail(FlowEvent4FlowFail flowEvent4FlowFail) {
        DeployFlowEntity deployFlow4Fail = flowEvent4FlowFail.getDeployFlowEntity();
        deployFlow4Fail.callFail();
        deployFlowMapper.updateFlowStatus(deployFlow4Fail);
        updateDeployCommandStatus(deployFlow4Fail);
        releaseAppLock(deployFlow4Fail);
    }

    @EventListener
    public void handleFlowEvent4FlowAllSuccess(FlowEvent4FlowAllSuccess flowEvent4FlowAllSuccess) {
        DeployFlowEntity deployFlowEntity = flowEvent4FlowAllSuccess.getDeployFlowEntity();
        updateDeployCommandStatus(deployFlowEntity);
        releaseAppLock(deployFlowEntity);
    }

    private void updateDeployCommandStatus(DeployFlowEntity deployFlowEntity) {
        DeployEntity deployEntity = getDeployCommand(deployFlowEntity);
        deployCommandMapper.updateDeployStatus(deployEntity);
    }

    private void releaseAppLock(DeployFlowEntity deployFlowEntity) {

    }

    private DeployEntity getDeployCommand(DeployFlowEntity deployFlowEntity) {
        return null;
    }

    private void excDeployFlow(DeployFlowEntity deployFlow) {
        deployFlow.callDeploy();
        deployFlowMapper.updateByPrimaryKey(deployFlow);
        DeployFlowExecStrategy deployFlowExecStrategy = StrategyNumBean.getBeanInstance(DeployFlowExecStrategy.class, deployFlow.getFlowType().name());
        deployFlowExecStrategy.execute(deployFlow);
    }


    private DeployFlowEntity getNextDeployFlow(DeployFlowEntity deployFlowEntity) {
        return null;
    }

    private boolean hasNextFlow(DeployFlowEntity nextDeployFlowEntity) {
        return nextDeployFlowEntity != null;
    }

}
