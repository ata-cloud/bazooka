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

import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.enums.deploy.DeployModeEnum;
import net.atayun.bazooka.deploy.biz.enums.flow.DeployFlowEnum;
import org.springframework.stereotype.Component;

/**
 * @author Ping
 * @date 2019-07-11
 */
@Component
@StrategyNum(superClass = AbstractDeployFlowStrategy.class,
        number = DeployModeEnum.MODE_BUILD,
        describe = "为构建发布初始化发布流程"
)
public class BuildDeployFlowStrategy extends AbstractDeployFlowStrategy {

    private static final DeployFlowEnum[] FLOWS = {
            DeployFlowEnum.SET_UP,
            DeployFlowEnum.PULL_CODE,
            DeployFlowEnum.BUILD_PROJECT,
            DeployFlowEnum.BUILD_DOCKER_IMAGE,
            DeployFlowEnum.DO_DEPLOY,
            DeployFlowEnum.HEALTH_CHECK,
    };


    /**
     * 发布步骤
     *
     * @return 发布步骤
     */
    @Override
    protected DeployFlowEnum[] getFlows() {
        return FLOWS;
    }
}
