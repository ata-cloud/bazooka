package net.atayun.bazooka.delpoy2.component.strategy.flow;


import net.atayun.bazooka.combase.enums.deploy.AppOperationEventLogTypeEnum;
import net.atayun.bazooka.deploy.biz.log.LogConcat;
import net.atayun.bazooka.deploy.biz.service.deploy.strategy.WorkDetailPojo;
import net.atayun.bazooka.pms.api.api.EnvApi;
import net.atayun.bazooka.pms.api.EnvDto;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 下午1:54
 */
public class DeployFlowExecStrategy4HealthCheck extends AbstractDeployFlowExecStrategy implements AsyncCallback {
    @Autowired
    private EnvApi envApi;


    @Override
    protected FlowExecuteResultEnum doExecute(WorkDetailPojo workDetail) {

        EnvDto envDto = envApi.get(workDetail.getAppDeployConfig().getEnvId())
                .ifNotSuccessThrowException()
                .getData();
        final LogConcat logConcat = new LogConcat(">> 1. 健康检查");
        String dcosServiceId = "/" + envDto.getCode() + workDetail.getAppInfo().getDcosServiceId();
        logConcat.concat("服务[" + dcosServiceId + "]正在发布...");
        getAppOperationEventLog().save(workDetail.getDeployEntity().getEventId(),
                AppOperationEventLogTypeEnum.APP_DEPLOY_FLOW_HEALTH_CHECK, 1, logConcat.get());
        return null;
    }

    @Override
    public void callback(Object marathonCallbackParam) {

    }

}
