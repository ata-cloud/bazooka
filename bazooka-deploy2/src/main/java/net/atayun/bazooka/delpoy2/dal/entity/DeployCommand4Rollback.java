package net.atayun.bazooka.delpoy2.dal.entity;

import net.atayun.bazooka.delpoy2.dal.entity.DeployCommand;
import net.atayun.bazooka.delpoy2.dto.DeployCommandReqDto;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 上午10:26
 */
public class DeployCommand4Rollback extends DeployCommand {
    public DeployCommand4Rollback(DeployCommandReqDto deployCommandReqDto) {
        super(deployCommandReqDto);
    }
}
