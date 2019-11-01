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
package net.atayun.bazooka.deploy.biz.service.deploy.cancel;

import mesosphere.marathon.client.Marathon;
import mesosphere.marathon.client.MarathonClient;
import net.atayun.bazooka.base.constant.CommonConstants;
import net.atayun.bazooka.deploy.biz.dal.entity.deploy.DeployEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowMarathonEntity;
import net.atayun.bazooka.deploy.biz.service.deploy.DeployService;
import net.atayun.bazooka.deploy.biz.service.flow.DeployFlowMarathonService;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.dto.ClusterConfigDto;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
@Component
public class MarathonFlowCancel implements FlowCancel {

    @Override
    public void cancel(DeployFlowEntity deployFlowEntity) {
        DeployFlowMarathonService flowMarathonService = getBean(DeployFlowMarathonService.class);
        DeployFlowMarathonEntity entity = flowMarathonService.selectByDeployFlowId(deployFlowEntity.getId());
        if (entity == null || StringUtils.isEmpty(entity.getMarathonDeploymentId())) {
            return;
        }
        DeployEntity deployEntity = getBean(DeployService.class).selectEntityById(deployFlowEntity.getDeployId());
        EnvApi envApi = getBean(EnvApi.class);
        ClusterConfigDto cluster = envApi.getEnvConfiguration(deployEntity.getEnvId()).ifNotSuccessThrowException().getData();
        String dcosUrl = CommonConstants.PROTOCOL + cluster.getDcosEndpoint() + CommonConstants.MARATHON_PORT;
        Marathon marathon = MarathonClient.getInstance(dcosUrl);
        marathon.cancelDeploymentAndRollback(entity.getMarathonDeploymentId());
    }
}
