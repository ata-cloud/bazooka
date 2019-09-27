package net.atayun.bazooka.delpoy2.service;

import net.atayun.bazooka.base.bean.StrategyNumBean;
import net.atayun.bazooka.delpoy2.dal.entity.DeployCommandMapper;
import net.atayun.bazooka.delpoy2.dal.entity.AppEnvDeployConfig;
import net.atayun.bazooka.delpoy2.dal.entity.DeployCommand;
import net.atayun.bazooka.delpoy2.dto.DeployCommandReqDto;
import net.atayun.bazooka.delpoy2.component.bridge.deploy.DeployBridge;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 上午10:07
 */
public class DeloyCommandServiceImpl {

    @Autowired
    private DeployCommandMapper deployCommandMapper;

    private AppEnvDeployConfigMapper appEnvDeployConfigMapper;

    public void executeCommand(DeployCommandReqDto deployCommandReqDto) {
        Long appEnvDeployConfigId = deployCommandReqDto.getAppEnvDeployConfigId();
        AppEnvDeployConfig appEnvDeployConfig = appEnvDeployConfigMapper.select(appEnvDeployConfigId);
        Long clusterId = appEnvDeployConfig.getClusterId();

        DeployCommand deployCommand = getDeployCommand(deployCommandReqDto);
        deployCommand.callDeploy();
        deployCommandMapper.insert(deployCommand);
        DeployBridge deployBridge = StrategyNumBean.getBeanInstance(DeployBridge.class, clusterId.toString());
        // App的服务已锁定，  1新建发布， 并且锁定deploy
        deployBridge.execute(deployCommand);
    }


    private DeployCommand getDeployCommand(DeployCommandReqDto deployCommandReqDto) {
        // TODO: 2019/9/25  
        return new DeployCommand();
    }


}
