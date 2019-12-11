package net.atayun.bazooka.deploy.biz.v2.service.app.opt;

import net.atayun.bazooka.base.bean.SpringApplicationEventPublisher;
import net.atayun.bazooka.base.bean.SpringContextBean;
import net.atayun.bazooka.base.constant.FlowStepConstants;
import net.atayun.bazooka.base.enums.FlowStepEnum;
import net.atayun.bazooka.deploy.biz.v2.constant.DeployConstants;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.FlowStepService;
import net.atayun.bazooka.deploy.biz.v2.service.app.flow.FlowDispatcherEvent;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.StepWorker;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.dto.EnvDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Ping
 */
public class AppOptWorker {

    private AppOpt appOpt;

    public AppOptWorker(AppOpt appOpt) {
        this.appOpt = appOpt;
    }

    public void doWork() {
        AppOpt appOpt = this.appOpt;
        //可后续拓展为自定义
        EnvDto env = SpringContextBean.getBean(EnvApi.class).get(appOpt.getEnvId())
                .ifNotSuccessThrowException().getData();
        String clusterType = env.getClusterType();
        List<String> stepTypes = appOpt.getOpt().defaultFlowStepTypes();
        if (Objects.equals(DeployConstants.NODE_CODE, clusterType)) {
            stepTypes = stepTypes.stream().filter(stepType -> !Objects.equals(stepType, FlowStepConstants.HEALTH_CHECK))
                    .collect(Collectors.toList());
        }

        List<String> finalStepTypes = new ArrayList<>(stepTypes);
        List<AppOptFlowStep> flowSteps = Stream.iterate(0, o -> ++o).limit(finalStepTypes.size())
                .map(index -> new AppOptFlowStepBuilder()
                        .setOptId(appOpt.getId())
                        .setSeq(index + 1)
                        .setStep(FlowStepEnum.valueOf(finalStepTypes.get(index)))
                        .build())
                .collect(Collectors.toList());
        FlowStepService flowStepService = SpringContextBean.getBean(FlowStepService.class);
        flowStepService.saveFlowSteps(flowSteps);
        AppOptFlowStep appOptFlowStep = flowSteps.get(0);
        Map<String, Object> detail = appOpt.getDetail();
        appOptFlowStep.setInput(detail);
        SpringApplicationEventPublisher.publish(new FlowDispatcherEvent(this, new StepWorker(appOpt, appOptFlowStep)));
    }
}
