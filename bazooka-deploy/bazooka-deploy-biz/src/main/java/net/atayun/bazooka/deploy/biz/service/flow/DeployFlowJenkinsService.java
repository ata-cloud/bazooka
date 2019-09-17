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
package net.atayun.bazooka.deploy.biz.service.flow;

import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowJenkinsEntity;

import java.util.List;

/**
 * @author Ping
 * @date 2019-07-23
 */
public interface DeployFlowJenkinsService {

    /**
     * 保存
     *
     * @param deployFlowJenkinsEntity deployFlowJenkinsEntity
     */
    void insertEntity(DeployFlowJenkinsEntity deployFlowJenkinsEntity);

    /**
     * 通过job信息查询
     *
     * @param jobName     job名称
     * @param buildNumber 构建号
     * @return list
     */
    List<DeployFlowJenkinsEntity> selectByJobInfoOrderByDeployFlowId(String jobName, Integer buildNumber);

    DeployFlowJenkinsEntity selectByDeployFlowId(Long deployFlowId);
}
