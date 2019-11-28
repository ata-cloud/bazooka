package net.atayun.bazooka.deploy.biz.v2.service.app.step;

import lombok.Getter;
import net.atayun.bazooka.base.bean.SpringApplicationEventPublisher;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.FlowStepService;
import net.atayun.bazooka.deploy.biz.v2.service.app.flow.FlowDispatcherEvent;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.log.StepLogCollector;
import org.springframework.beans.factory.annotation.Autowired;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
public abstract class Step implements Cancel {

    @Getter
    @Autowired
    private StepLogCollector stepLogCollector;

    protected abstract void doWork(AppOpt appOpt, AppOptFlowStep appOptFlowStep);

    /**
     * AppOptFlowStep 完成后 需要更新flow step, 并发送FlowDispatcherEvent事件
     *
     * @param appOpt         appOpt
     * @param appOptFlowStep appOptFlowStep
     */
    public void updateFlowStepAndPublish(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        //执行过程中可能被取消
        FlowStepService flowStepService = getBean(FlowStepService.class);
        AppOptFlowStep mayCancel = flowStepService.selectById(appOptFlowStep.getId());
        if (mayCancel.isCancel()) {
            appOptFlowStep.cancel();
        }
        //更新
        flowStepService.update(appOptFlowStep);
        SpringApplicationEventPublisher.publish(new FlowDispatcherEvent(this, new StepWorker(appOpt, appOptFlowStep)));
    }

    @Override
    public void cancel(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        getBean(FlowStepService.class).cancel(appOptFlowStep);
        //也可由各步骤自定义实现...
    }
}
