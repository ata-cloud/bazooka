package net.atayun.bazooka.delpoy2.component.listener;

import lombok.Builder;
import lombok.Data;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/26 下午4:45
 */
@Data
@Builder
public class FlowEvent4StartDeploy extends FlowEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source           the object on which the event initially occurred (never {@code null})
     * @param deployFlowEntity
     */
    public FlowEvent4StartDeploy(Object source, DeployFlowEntity deployFlowEntity) {
        super(source, deployFlowEntity);
    }

}
