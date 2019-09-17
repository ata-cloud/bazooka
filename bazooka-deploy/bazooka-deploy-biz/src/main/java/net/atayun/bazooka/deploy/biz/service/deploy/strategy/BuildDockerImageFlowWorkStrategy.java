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
package net.atayun.bazooka.deploy.biz.service.deploy.strategy;

import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.bean.SpringContextBean;
import net.atayun.bazooka.base.enums.deploy.AppOperationEventLogTypeEnum;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;
import net.atayun.bazooka.deploy.biz.enums.flow.DeployFlowEnum;
import net.atayun.bazooka.deploy.biz.log.LogConcat;
import net.atayun.bazooka.deploy.biz.param.jenkins.BuildScriptCallbackParam;
import net.atayun.bazooka.deploy.biz.service.deploy.cancel.JenkinsFlowCancel;
import net.atayun.bazooka.deploy.biz.service.jenkins.JenkinsService;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.dto.ClusterConfigDto;
import org.springframework.stereotype.Component;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 * @date 2019-07-12
 */
@Component
@StrategyNum(
        superClass = AbstractDeployFlowWorkStrategy.class,
        number = DeployFlowEnum.FLOW_BUILD_DOCKER_IMAGE,
        describe = "打包镜像步骤")
public class BuildDockerImageFlowWorkStrategy extends AbstractDeployFlowWorkStrategy {

    /**
     * 发布每个流程的具体实现
     *
     * @param workDetailPojo Flow所需所有数据实体
     * @see JenkinsService#buildScriptCallback(BuildScriptCallbackParam)
     */
    @Override
    public boolean doWorkDetail(WorkDetailPojo workDetailPojo) {
        AppDeployConfigDto appDeployConfig = workDetailPojo.getAppDeployConfig();
        ClusterConfigDto clusterConfig = getBean(EnvApi.class).getEnvConfiguration(appDeployConfig.getEnvId())
                .ifNotSuccessThrowException()
                .getData();
        final LogConcat logConcat = new LogConcat(">> 1. 构建Docker镜像相关信息");
        logConcat.concat("Docker Registry: " + clusterConfig.getDockerHubUrl());
        logConcat.concat("镜像名称: " + workDetailPojo.getAppInfo().getDockerImageName());
        logConcat.concat("Dockerfile相对路径: " + appDeployConfig.getDockerfilePath());
        getAppOperationEventLog().save(workDetailPojo.getDeployEntity().getEventId(),
                AppOperationEventLogTypeEnum.APP_DEPLOY_FLOW_BUILD_DOCKER_IMAGE, 1, logConcat.get());
        return false;
    }

    @Override
    public void cancel(DeployFlowEntity deployFlowEntity) {
        SpringContextBean.getBean(JenkinsFlowCancel.class).cancel(deployFlowEntity);
    }
}
