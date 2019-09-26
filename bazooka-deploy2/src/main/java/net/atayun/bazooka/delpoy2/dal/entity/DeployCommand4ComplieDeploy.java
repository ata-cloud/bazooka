package net.atayun.bazooka.delpoy2.dal.entity;

import net.atayun.bazooka.delpoy2.dal.entity.DeployCommand;
import net.atayun.bazooka.delpoy2.dto.DeployCommandReqDto;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 上午10:36
 */
public class DeployCommand4ComplieDeploy extends DeployCommand {
    public DeployCommand4ComplieDeploy(DeployCommandReqDto deployCommandReqDto) {
        super(deployCommandReqDto);
    }

    @Override
    public void execute() {

    }
}
