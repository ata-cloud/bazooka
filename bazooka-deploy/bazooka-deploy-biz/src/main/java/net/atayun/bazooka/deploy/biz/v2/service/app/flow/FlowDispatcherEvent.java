package net.atayun.bazooka.deploy.biz.v2.service.app.flow;

import lombok.Getter;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.StepWorker;
import org.springframework.context.ApplicationEvent;

/**
 * @author Ping
 */
public class FlowDispatcherEvent extends ApplicationEvent {

    @Getter
    private StepWorker stepWorker;

    public FlowDispatcherEvent(Object source, StepWorker stepWorker) {
        super(source);
        this.stepWorker = stepWorker;
    }
}
