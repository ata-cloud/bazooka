package net.atayun.bazooka.delpoy2.component.listener;

import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/26 下午4:49
 */
public class FlowEvent4FlowAllSuccess extends FlowEvent{
    /**
     * Create a new ApplicationEvent.
     *
     * @param source           the object on which the event initially occurred (never {@code null})
     * @param deployFlowEntity
     */
    public FlowEvent4FlowAllSuccess(Object source, DeployFlowEntity deployFlowEntity) {
        super(source, deployFlowEntity);
    }
}
