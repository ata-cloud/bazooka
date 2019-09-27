package net.atayun.bazooka.delpoy2.component.listener;

import lombok.Data;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;
import org.springframework.context.ApplicationEvent;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/26 下午4:49
 */
@Data
public class FlowEvent extends ApplicationEvent {

    private DeployFlowEntity deployFlowEntity;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public FlowEvent(Object source, DeployFlowEntity deployFlowEntity) {
        super(source);
        this.deployFlowEntity = deployFlowEntity;
    }

}
