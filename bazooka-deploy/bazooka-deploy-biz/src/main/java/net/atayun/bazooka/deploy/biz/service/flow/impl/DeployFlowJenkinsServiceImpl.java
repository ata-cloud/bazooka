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

import com.youyu.common.service.AbstractService;
import net.atayun.bazooka.deploy.biz.dal.dao.flow.DeployFlowJenkinsMapper;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowJenkinsEntity;
import net.atayun.bazooka.deploy.biz.dto.flow.DeployFlowJenkinsDto;
import net.atayun.bazooka.deploy.biz.service.flow.DeployFlowJenkinsService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author Ping
 * @date 2019-07-23
 */
@Service
public class DeployFlowJenkinsServiceImpl
        extends AbstractService<Long, DeployFlowJenkinsDto, DeployFlowJenkinsEntity, DeployFlowJenkinsMapper>
        implements DeployFlowJenkinsService {

    /**
     * 保存
     *
     * @param deployFlowJenkinsEntity deployFlowJenkinsEntity
     */
    @Override
    public void insertEntity(DeployFlowJenkinsEntity deployFlowJenkinsEntity) {
        getMapper().insertSelective(deployFlowJenkinsEntity);
    }

    /**
     * 通过job信息查询
     *
     * @param jobName     job名称
     * @param buildNumber 构建号
     * @return list
     */
    @Override
    public List<DeployFlowJenkinsEntity> selectByJobInfoOrderByDeployFlowId(String jobName, Integer buildNumber) {
        Example example = new Example(DeployFlowJenkinsEntity.class);
        example.createCriteria().andEqualTo("jobName", jobName).andEqualTo("jobBuildNumber", buildNumber);
        example.orderBy("deployFlowId");
        return getMapper().selectByExample(example);
    }

    @Override
    public DeployFlowJenkinsEntity selectByDeployFlowId(Long deployFlowId) {
        Example example = new Example(DeployFlowJenkinsEntity.class);
        example.createCriteria().andEqualTo("deployFlowId", deployFlowId);
        return getMapper().selectOneByExample(example);
    }
}
