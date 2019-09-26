package net.atayun.bazooka.delpoy2.service;

import net.atayun.bazooka.base.bean.StrategyNumBean;
import net.atayun.bazooka.delpoy2.dal.DeployCommandMapper;
import net.atayun.bazooka.delpoy2.dal.entity.AppEnvDeployConfig;
import net.atayun.bazooka.delpoy2.dal.entity.DeployCommand;
import net.atayun.bazooka.delpoy2.dto.DeployCommandReqDto;
import net.atayun.bazooka.delpoy2.strategy.DeployCommandStrategy;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 上午10:07
 */
public class DeloyCommandServiceImpl {


    private DeployCommandMapper deployCommandMapper;

    private AppEnvDeployConfigMapper appEnvDeployConfigMapper;

    public void executeCommand(DeployCommandReqDto deployCommandReqDto){
        Long appEnvDeployConfigId = deployCommandReqDto.getAppEnvDeployConfigId();
        AppEnvDeployConfig appEnvDeployConfig = appEnvDeployConfigMapper.select(appEnvDeployConfigId);
        Long clusterId = appEnvDeployConfig.getClusterId();

        DeployCommand deployCommand = getDeployCommand(deployCommandReqDto);

        DeployCommandStrategy deployCommandStrategy = StrategyNumBean.getBeanInstance(DeployCommandStrategy.class, clusterId.toString());
        deployCommandStrategy.execute(deployCommand);
    }













    private DeployCommand getDeployCommand(DeployCommandReqDto deployCommandReqDto) {
        // TODO: 2019/9/25  
        return new DeployCommand();
    }




}
