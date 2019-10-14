package net.atayun.bazooka.delpoy2.component.strategy.flow;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.atayun.bazooka.delpoy2.component.listener.FlowEvent4FlowFail;
import net.atayun.bazooka.delpoy2.component.listener.FlowEvent4FlowSuccess;
import net.atayun.bazooka.deploy.biz.dal.entity.deploy.DeployEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;
import net.atayun.bazooka.deploy.biz.log.AppOperationEventLog;
import net.atayun.bazooka.deploy.biz.service.deploy.DeployService;
import net.atayun.bazooka.deploy.biz.service.deploy.strategy.WorkDetailPojo;
import net.atayun.bazooka.deploy.biz.service.flow.DeployFlowService;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.dto.AppInfoWithCredential;
import net.atayun.bazooka.pms.api.feign.AppApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/27 上午10:26
 */
@Slf4j
public abstract class AbstractDeployFlowExecStrategy implements DeployFlowExecStrategy {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DeployFlowService deployFlowService;

    @Autowired
    private AppApi appApi;

    @Autowired
    private DeployService deployService;

    @Getter
    @Autowired
    private AppOperationEventLog appOperationEventLog;
    @Autowired
    ApplicationEventPublisher eventPublisher;


    /**
     * 发布每个流程的具体实现, 执行发布流程， 并且发送 相应事件（状态变更一律有调度器控制）
     *
     * @param deployFlowEntity
     */
    @Override
    public void execute(DeployFlowEntity deployFlowEntity) {
        FlowExecuteResultEnum executeResult = null;
        try {
            WorkDetailPojo workDetail = initWorkDetailPoJo(deployFlowEntity);
            executeResult = doExecute(workDetail);
        } catch (Exception e) {
            eventPublisher.publishEvent(new FlowEvent4FlowFail(this, deployFlowEntity));
            log.warn(String.format("流程异常[deployId: %s, flowNumber: %s, ]", deployFlowEntity.getDeployId(), deployFlowEntity.getFlowNumber()), e);
        }
        if (isAsyncFlowExecute()) {
            return;
        }
        if (executeResult == FlowExecuteResultEnum.SUCCESS) {
            eventPublisher.publishEvent(new FlowEvent4FlowSuccess(this, deployFlowEntity));
            return;
        }
        eventPublisher.publishEvent(new FlowEvent4FlowFail(this, deployFlowEntity));
        return;
    }


    boolean isAsyncFlowExecute() {
//          this.getClass().getAnnotatedInterfaces()  AsyncCallback
        return false;
    }


    protected abstract FlowExecuteResultEnum doExecute(WorkDetailPojo workDetail);

    /**
     * 初始化发布流程所需数据
     *
     * @param deployFlowEntity deployFlowEntity
     * @return WorkDetailPojo
     */
    private WorkDetailPojo initWorkDetailPoJo(DeployFlowEntity deployFlowEntity) {
        DeployEntity deployEntity = deployService.selectEntityById(deployFlowEntity.getDeployId());

        AppDeployConfigDto appDeployConfig = appApi.getAppDeployConfigInfoById(deployEntity.getDeployConfigId())
                .ifNotSuccessThrowException()
                .getData();

        AppInfoWithCredential appInfo = appApi.getAppInfoWithCredentialById(deployEntity.getAppId())
                .ifNotSuccessThrowException()
                .getData();

        return new WorkDetailPojo(deployEntity, deployFlowEntity, appInfo, appDeployConfig);
    }

}
