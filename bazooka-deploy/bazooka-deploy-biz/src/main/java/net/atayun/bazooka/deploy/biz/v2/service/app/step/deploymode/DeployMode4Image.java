package net.atayun.bazooka.deploy.biz.v2.service.app.step.deploymode;

import com.youyu.common.exception.BizException;
import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.constant.CommonConstants;
import net.atayun.bazooka.base.docker.DockerRegistryService;
import net.atayun.bazooka.base.docker.domain.DockerImageTags;
import net.atayun.bazooka.base.enums.deploy.DeployModeEnum;
import net.atayun.bazooka.deploy.biz.v2.constant.DeployConstants;
import net.atayun.bazooka.deploy.biz.v2.constant.DeployResultCodeConstants;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.log.StepLogBuilder;
import net.atayun.bazooka.pms.api.dto.AppInfoDto;
import net.atayun.bazooka.pms.api.feign.AppApi;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.dto.ClusterConfigDto;
import net.atayun.bazooka.rms.api.dto.EnvDto;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = DeployMode.class, number = DeployModeEnum.MODE_DOCKER_IMAGE)
public class DeployMode4Image implements DeployMode {

    @Override
    public void check(AppOpt appOpt, AppOptFlowStep appOptFlowStep, StepLogBuilder stepLogBuilder) {
        //检查镜像是否存在
        ClusterConfigDto clusterConfig = getBean(EnvApi.class).getEnvConfiguration(appOpt.getEnvId())
                .ifNotSuccessThrowException().getData();
        String dockerHubUrl = clusterConfig.getDockerHubUrl();

        AppInfoDto appInfoDto = getBean(AppApi.class).getAppInfoById(appOpt.getAppId())
                .ifNotSuccessThrowException().getData();
        String dockerImageName = appInfoDto.getDockerImageName();

        String dockerImageTag = appOpt.getFinalDockerImageTag();

        String image = dockerHubUrl + "/" + dockerImageName + ":" + dockerImageTag;

        stepLogBuilder.append("检查镜像: " + image);

        EnvDto env = getBean(EnvApi.class).get(appOpt.getEnvId()).ifNotSuccessThrowException().getData();
        if (!Objects.equals(env.getClusterType(), DeployConstants.NODE_CODE)) {
            DockerImageTags dockerImageTags = getBean(DockerRegistryService.class)
                    .listImageTags(CommonConstants.PROTOCOL + dockerHubUrl, dockerImageName);
            List<String> tags = dockerImageTags.getTags();
            if (CollectionUtils.isEmpty(tags) || !tags.contains(dockerImageTag)) {
                String msg = String.format("镜像库[%s]中不存在该镜像[%s]", dockerHubUrl, image);
                throw new BizException(DeployResultCodeConstants.IMAGE_EMPTY, msg);
            }
        }
        Map<String, Object> output = appOptFlowStep.getNotNullOutput();
        output.put("dockerImage", image);
    }

}
