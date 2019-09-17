/*
 *    Copyright 2018-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.atayun.bazooka.deploy.biz.service.flow.impl;

import net.atayun.bazooka.deploy.biz.dal.dao.flow.DeployFlowImageMapper;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowImageEntity;
import net.atayun.bazooka.deploy.biz.dto.flow.DeployFlowImageDto;
import net.atayun.bazooka.deploy.biz.service.flow.DeployFlowImageService;
import com.youyu.common.service.AbstractService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * @author Ping
 * @date 2019-07-23
 */
@Service
public class DeployFlowImageServiceImpl
        extends AbstractService<Long, DeployFlowImageDto, DeployFlowImageEntity, DeployFlowImageMapper>
        implements DeployFlowImageService {
    /**
     * insert
     *
     * @param deployFlowImageEntity deployFlowImageEntity
     * @return rows
     */
    @Override
    public int insertEntity(DeployFlowImageEntity deployFlowImageEntity) {
        return getMapper().insertSelective(deployFlowImageEntity);
    }

    /**
     * 通过deploy flow id查询
     *
     * @param deployFlowId deployFlowId
     * @return DeployFlowImageEntity
     */
    @Override
    public DeployFlowImageEntity selectByDeployFlowId(Long deployFlowId) {
        Example example = new Example(DeployFlowImageEntity.class);
        example.createCriteria().andEqualTo("deployFlowId", deployFlowId);
        return getMapper().selectOneByExample(example);
    }
}
