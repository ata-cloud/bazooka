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
package net.atayun.bazooka.deploy.biz.service.deploy.strategy.setup;

import net.atayun.bazooka.combase.annotation.StrategyNum;
import net.atayun.bazooka.combase.constant.CommonConstants;
import net.atayun.bazooka.combase.docker.DockerRegistryService;
import net.atayun.bazooka.combase.docker.domain.DockerImageTags;
import net.atayun.bazooka.combase.enums.deploy.AppOperationEventLogTypeEnum;
import net.atayun.bazooka.combase.enums.deploy.DeployModeEnum;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowImageEntity;
import net.atayun.bazooka.deploy.biz.log.LogConcat;
import net.atayun.bazooka.deploy.biz.service.deploy.strategy.WorkDetailPojo;
import net.atayun.bazooka.deploy.biz.service.flow.DeployFlowImageService;
import net.atayun.bazooka.pms.api.dto.AppInfoWithCredential;
import net.atayun.bazooka.pms.api.api.EnvApi;
import net.atayun.bazooka.pms.api.dto.ClusterConfigDto;
import com.youyu.common.exception.BizException;
import net.atayun.bazooka.deploy.biz.constants.DeployResultCodeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author Ping
 * @date 2019-07-22
 */
@Component
@StrategyNum(superClass = AbstractSetUpFlowStrategy.class,
        number = DeployModeEnum.MODE_DOCKER_IMAGE,
        describe = "镜像发布的预备工作"
)
public class DockerImageDeploySetUpStrategy extends AbstractSetUpFlowStrategy {

    @Autowired
    private DockerRegistryService dockerRegistryService;

    @Autowired
    private EnvApi envApi;

    @Autowired
    private DeployFlowImageService deployFlowImageService;

    /**
     * 发布流中 sep_up中的部分资源检查
     * {@link DeployModeEnum#DOCKER_IMAGE} 需要校验镜像是否存在且有效
     *
     * @param workDetailPojo 实体数据
     */
    @Override
    public void setUpCheck(WorkDetailPojo workDetailPojo) {
        AppInfoWithCredential appInfo = workDetailPojo.getAppInfo();
        String dockerImageName = appInfo.getDockerImageName();
        String dockerImageTag = workDetailPojo.getDeployEntity().getDockerImageTag();
        final LogConcat logConcat = new LogConcat(">> 1. 检查镜像(" + dockerImageName + ":" + dockerImageTag + ")");
        try {
            ClusterConfigDto clusterConfig = envApi.getEnvConfiguration(workDetailPojo.getAppDeployConfig().getEnvId())
                    .ifNotSuccessThrowException()
                    .getData();

            String dockerHubUrl = clusterConfig.getDockerHubUrl();
            DockerImageTags dockerImageTags = dockerRegistryService.listImageTags(CommonConstants.PROTOCOL + dockerHubUrl, dockerImageName);
            List<String> tags = dockerImageTags.getTags();
            if (CollectionUtils.isEmpty(tags)) {
                throw new BizException(DeployResultCodeConstants.IMAGE_EMPTY,
                        String.format("镜像库[%s]中没有%s镜像", dockerHubUrl, dockerImageName));
            }

            if (!tags.contains(dockerImageTag)) {
                throw new BizException(DeployResultCodeConstants.ILLEGAL_IMAGE_TAG,
                        String.format("镜像库[%s]中没有镜像%s:%s", dockerHubUrl, dockerImageName, dockerImageTag));
            }
            logConcat.concat("镜像符合要求");
        } catch (Throwable throwable) {
            logConcat.concat(throwable);
            throw throwable;
        } finally {
            getAppOperationEventLog().save(workDetailPojo.getDeployEntity().getEventId(),
                    AppOperationEventLogTypeEnum.APP_DEPLOY_FLOW_SET_UP, 1, logConcat.get());
        }
    }

    /**
     * 准备工作完成后的操作
     *
     * @param workDetailPojo 实体数据
     */
    @Override
    public void afterAll(WorkDetailPojo workDetailPojo) {
        DeployFlowImageEntity deployFlowImageEntity = new DeployFlowImageEntity();
        deployFlowImageEntity.setDeployFlowId(workDetailPojo.getDeployFlowEntity().getId());
        deployFlowImageEntity.setDockerImageName(workDetailPojo.getAppInfo().getDockerImageName());
        deployFlowImageEntity.setDockerImageTag(workDetailPojo.getDeployEntity().getDockerImageTag());
        deployFlowImageService.insertEntity(deployFlowImageEntity);
    }
}
