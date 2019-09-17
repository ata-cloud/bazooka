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
package net.atayun.bazooka.deploy.biz.service.flow.strategy;

import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;
import net.atayun.bazooka.deploy.biz.enums.flow.DeployFlowEnum;
import net.atayun.bazooka.deploy.biz.enums.status.BasicStatusEnum;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Ping
 * @date 2019-07-11
 */
public abstract class AbstractDeployFlowStrategy {

    /**
     * FIRST_FLOW_NUMBER
     */
    public static final int FIRST_FLOW_NUMBER = 1;

    /**
     * 初始化发布步骤
     *
     * @param deployId deployId
     * @return 发布步骤
     */
    public List<DeployFlowEntity> initFlow(Long deployId) {
        DeployFlowEnum[] flows = getFlows();
        return Stream.iterate(FIRST_FLOW_NUMBER, o -> ++o).limit(flows.length)
                .map(flowNumber -> {
                    DeployFlowEnum flow = flows[flowNumber - 1];
                    DeployFlowEntity deployFlowEntity = new DeployFlowEntity();
                    deployFlowEntity.setDeployId(deployId);
                    deployFlowEntity.setFlowNumber(flowNumber);
                    deployFlowEntity.setFlowType(flow);
                    deployFlowEntity.setFlowStatus(BasicStatusEnum.STAND_BY);
                    return deployFlowEntity;
                })
                .collect(Collectors.toList());
    }

    /**
     * 发布步骤
     *
     * @return 发布步骤
     */
    protected abstract DeployFlowEnum[] getFlows();
}
