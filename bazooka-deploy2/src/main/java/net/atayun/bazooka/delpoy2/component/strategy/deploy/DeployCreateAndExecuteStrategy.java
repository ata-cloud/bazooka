package net.atayun.bazooka.delpoy2.component.strategy.deploy;

import net.atayun.bazooka.delpoy2.component.listener.FlowEvent4StartDeploy;
import net.atayun.bazooka.delpoy2.component.strategy.flow.DeployFlowExecStrategy;
import net.atayun.bazooka.delpoy2.dal.dao.DeployCommandMapper;
import net.atayun.bazooka.delpoy2.dal.entity.DeployEntity;
import net.atayun.bazooka.delpoy2.dto.DeployCommandReqDto;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;
import net.atayun.bazooka.deploy.biz.enums.status.BasicStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 上午10:51
 */
public abstract class DeployCreateAndExecuteStrategy {

    protected List<DeployFlowExecStrategy> deployFlowExecStrategyList;

    @Autowired
    ApplicationEventPublisher eventPublisher;
    @Autowired
    private DeployCommandMapper deployCommandMapper;

    public void setDeployActionStrategyList(List<DeployFlowExecStrategy> deployFlowExecStrategyList) {
        this.deployFlowExecStrategyList = deployFlowExecStrategyList;
    }

    public void createAndExecute(DeployCommandReqDto deployCommandReqDto) {
        DeployEntity deployEntity = create(deployCommandReqDto);

        execute(deployEntity);
    }

    /**
     * 差异化创建DeployEntity
     *
     * @param deployCommandReqDto
     * @return
     */
    protected abstract DeployEntity create(DeployCommandReqDto deployCommandReqDto);

    /**
     * @param deployEntity
     */
    protected void execute(DeployEntity deployEntity) {
        processDeploy(deployEntity);

        createDeployFlowsAndPublishEvent(deployEntity.getDeployConfigId());
    }

    protected void processDeploy(DeployEntity deployEntity) {
        deployEntity.setStatus(BasicStatusEnum.PROCESS);
        deployEntity.setUpdateTime(LocalDateTime.now());
        deployCommandMapper.insertSelective(deployEntity);
    }

    protected void createDeployFlowsAndPublishEvent(Long deployConfigId) {
        List<DeployFlowEntity> deployFlowEntities = createFlows(deployConfigId, deployFlowExecStrategyList);
        eventPublisher.publishEvent(new FlowEvent4StartDeploy(this, deployFlowEntities.get(0)));
    }

    /**
     * 构造发布指令
     *
     * @param deployCommand
     * @return
     */
    protected abstract DeployEntity buildDeployCommand(DeployCommandReqDto deployCommand);


    private void batchSave(List<DeployFlowEntity> deployFlowEntities) {


        //TODO
    }


    private List<DeployFlowEntity> createFlows(Long appEnvDeployConfigId, List<DeployFlowExecStrategy> deployFlowExecStrategyList) {
        //TODO
        return null;
    }
}
