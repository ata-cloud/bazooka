package net.atayun.bazooka.delpoy2.dal.dao;

import com.youyu.common.mapper.YyMapper;
import net.atayun.bazooka.delpoy2.dal.entity.DeployEntity;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 上午10:17
 */
public interface DeployCommandMapper extends YyMapper<DeployEntity> {
    void updateDeployStatus(DeployEntity deployEntity);
}
