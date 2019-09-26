package net.atayun.bazooka.delpoy2.dal.entity;

import net.atayun.bazooka.delpoy2.dal.entity.DeployCommand;
import net.atayun.bazooka.delpoy2.dto.DeployCommandReqDto;
import net.atayun.bazooka.delpoy2.strategy.DeployCommandStrategy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 上午10:33
 */
public class DeployCommand4ReStart extends DeployCommand {

    @Autowired
    private DeployCommandStrategy deployCommandStrategy;

    public DeployCommand4ReStart(DeployCommandReqDto deployCommandReqDto) {
        super(deployCommandReqDto);
    }

    @Override
    public void execute() {

    }
}
