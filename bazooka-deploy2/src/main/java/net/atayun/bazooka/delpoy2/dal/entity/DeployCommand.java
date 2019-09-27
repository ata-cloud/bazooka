package net.atayun.bazooka.delpoy2.dal.entity;

import com.youyu.common.entity.BaseEntity;
import lombok.Data;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 上午10:15
 */
@Data
public class DeployCommand extends BaseEntity<Long> {

    protected Long id;

    private Long appEnvDeployConfigId;

    public void callDeploy() {

    }
}