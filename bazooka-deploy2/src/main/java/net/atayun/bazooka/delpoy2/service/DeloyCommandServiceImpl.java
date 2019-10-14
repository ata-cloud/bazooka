package net.atayun.bazooka.delpoy2.service;

import net.atayun.bazooka.combase.bean.StrategyNumBean;
import net.atayun.bazooka.delpoy2.component.strategy.flow.DeployFlowExecStrategy;
import net.atayun.bazooka.delpoy2.dal.dao.AppEnvDeployConfigMapper;
import net.atayun.bazooka.delpoy2.dal.dao.DeployCommandMapper;
import net.atayun.bazooka.delpoy2.dal.entity.DeployEntity;
import net.atayun.bazooka.delpoy2.dto.DeployCommandReqDto;
import net.atayun.bazooka.delpoy2.component.strategy.deploy.DeployCreateAndExecuteStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 上午10:07
 */
public class DeloyCommandServiceImpl {

    @Autowired
    private DeployCommandMapper deployCommandMapper;

    private AppEnvDeployConfigMapper appEnvDeployConfigMapper;

    public void executeCommand(DeployCommandReqDto deployCommandReqDto) {

        //一次发布请求， 找到对应的发布动作，创建发布实体， 由发布动作执行发布实体。 动作执行的成功，失败发送相应的 事件
        DeployCreateAndExecuteStrategy deployCreateAndExecuteStrategy = getDeployBridge(deployCommandReqDto);
        deployCreateAndExecuteStrategy.createAndExecute(deployCommandReqDto);
    }

    private DeployCreateAndExecuteStrategy getDeployBridge(DeployCommandReqDto deployCommandReqDto) {
        String deployActions = getDeployActions(deployCommandReqDto);
        DeployCreateAndExecuteStrategy deployCreateAndExecuteStrategy = StrategyNumBean.getBeanInstance(DeployCreateAndExecuteStrategy.class, deployCommandReqDto.getAppOperationEnum().name());
        List<DeployFlowExecStrategy> deployFlowStrategies = StrategyNumBean.getBeanInstances(DeployFlowExecStrategy.class, deployActions);
        deployCreateAndExecuteStrategy.setDeployActionStrategyList(deployFlowStrategies);
        return deployCreateAndExecuteStrategy;
    }

    private String getDeployActions(DeployCommandReqDto deployCommandReqDto) {
        return null;
    }


    private DeployEntity getDeployCommand(DeployCommandReqDto deployCommandReqDto) {
        // TODO: 2019/9/25  
        return new DeployEntity();
    }


}
