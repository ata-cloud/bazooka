package net.atayun.bazooka.deploy.biz.v2.service.app.opt;

import net.atayun.bazooka.base.bean.SpringApplicationEventPublisher;
import net.atayun.bazooka.base.bean.SpringContextBean;
import net.atayun.bazooka.base.enums.FlowStepEnum;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.FlowStepService;
import net.atayun.bazooka.deploy.biz.v2.service.app.flow.FlowDispatcherEvent;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.StepWorker;

import java.util.List;
import java.util.Map;
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
        List<String> stepTypes = appOpt.getOpt().defaultFlowStepTypes();

        List<AppOptFlowStep> flowSteps = Stream.iterate(0, o -> ++o).limit(stepTypes.size())
                .map(index -> new AppOptFlowStepBuilder()
                        .setOptId(appOpt.getId())
                        .setSeq(index + 1)
                        .setStep(FlowStepEnum.valueOf(stepTypes.get(index)))
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
