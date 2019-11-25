package net.atayun.bazooka.deploy.biz.v2.service.app.opt;

import net.atayun.bazooka.base.bean.SpringApplicationEventPublisher;
import net.atayun.bazooka.base.bean.SpringContextBean;
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
        //可后续拓展自定义
        List<String> stepTypes = appOpt.getOpt().defaultFlowStepTypes();

        Map<String, Object> detail = appOpt.getDetail();
        List<AppOptFlowStep> flowSteps = Stream.iterate(0, o -> ++o).limit(stepTypes.size())
                .map(index -> new AppOptFlowStepBuilder()
                        .setOptId(appOpt.getId())
                        .setSeq(index + 1)
                        .setStep(stepTypes.get(index))
                        .setInput(detail)
                        .build())
                .collect(Collectors.toList());
        FlowStepService flowStepService = SpringContextBean.getBean(FlowStepService.class);
        flowStepService.saveFlowStep(flowSteps);

        StepWorker stepWorker = new StepWorker(appOpt, flowSteps.get(0));
        SpringApplicationEventPublisher.publish(new FlowDispatcherEvent(this, stepWorker));
    }
}
