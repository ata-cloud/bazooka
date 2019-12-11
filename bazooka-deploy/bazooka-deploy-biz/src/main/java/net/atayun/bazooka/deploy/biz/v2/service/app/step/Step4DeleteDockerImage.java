package net.atayun.bazooka.deploy.biz.v2.service.app.step;

import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.constant.FlowStepConstants;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.log.StepLogBuilder;
import net.atayun.bazooka.rms.api.RmsDockerImageApi;
import net.atayun.bazooka.rms.api.dto.RmsDockerImageDto;
import org.springframework.stereotype.Component;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = Step.class, number = FlowStepConstants.DELETE_DOCKER_IMAGE)
public class Step4DeleteDockerImage extends Step implements SinglePhase {

    @Override
    public void doWork(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        StepLogBuilder stepLogBuilder = new StepLogBuilder();
        stepLogBuilder.append("镜像Id:" + appOpt.getImageId());
        Long imageId = appOpt.getImageId();
        RmsDockerImageApi rmsDockerImageApi = getBean(RmsDockerImageApi.class);
        try {
            RmsDockerImageDto imageDto = rmsDockerImageApi.get(imageId).ifNotSuccessThrowException().getData();
            stepLogBuilder.append("镜像Tag:" + imageDto.getImageTag());
            rmsDockerImageApi.delete(imageId).ifNotSuccessThrowException();
        } catch (Throwable throwable) {
            stepLogBuilder.append(throwable);
            throw throwable;
        } finally {
            getStepLogCollector().collect(appOptFlowStep, stepLogBuilder.build());
        }
    }
}
