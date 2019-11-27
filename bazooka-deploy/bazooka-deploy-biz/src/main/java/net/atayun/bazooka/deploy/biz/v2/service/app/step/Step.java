package net.atayun.bazooka.deploy.biz.v2.service.app.step;

import net.atayun.bazooka.base.bean.SpringApplicationEventPublisher;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.enums.FlowStepStatusEnum;
import net.atayun.bazooka.deploy.biz.v2.service.app.flow.FlowDispatcherEvent;

/**
 * @author Ping
 */
public abstract class Step implements Cancel {

    protected abstract void doWork(AppOpt appOpt, AppOptFlowStep appOptFlowStep);

    public void notification(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        SpringApplicationEventPublisher.publish(new FlowDispatcherEvent(this, new StepWorker(appOpt, appOptFlowStep)));
    }

    @Override
    public void cancel(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {

    }
}
