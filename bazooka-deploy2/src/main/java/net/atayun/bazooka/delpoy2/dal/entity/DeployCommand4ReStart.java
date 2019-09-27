package net.atayun.bazooka.delpoy2.dal.entity;

import net.atayun.bazooka.delpoy2.component.bridge.deploy.DeployBridge;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 上午10:33
 */
public class DeployCommand4ReStart extends DeployCommand {

    @Autowired
    private DeployBridge deployBridge;

}
