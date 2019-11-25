package net.atayun.bazooka.deploy.biz.v2.service.app.platform;

import mesosphere.client.common.ModelUtils;
import mesosphere.marathon.client.Marathon;
import mesosphere.marathon.client.MarathonClient;
import mesosphere.marathon.client.model.v2.App;
import mesosphere.marathon.client.model.v2.Result;
import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.constant.CommonConstants;
import net.atayun.bazooka.deploy.biz.constants.MarathonAppConfigConstants;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppOptService;
import net.atayun.bazooka.pms.api.dto.AppInfoWithCredential;
import net.atayun.bazooka.pms.api.feign.AppApi;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.dto.ClusterConfigDto;
import net.atayun.bazooka.rms.api.dto.EnvDto;
import org.springframework.stereotype.Component;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = Platform.class, number = "MESOS")
public class Platform4Marathon implements Platform {

    @Override
    public void startApp(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        AppOptService appOptService = getBean(AppOptService.class);

        //app
        AppOpt lastAppOpt = appOptService.selectLastSuccessByAppIdAndEnv(appOpt.getAppId(), appOpt.getEnvId());
        String deployConfig = lastAppOpt.getAppDeployConfig();
        App app = ModelUtils.GSON.fromJson(deployConfig, App.class);
        Integer instance = (Integer) appOptFlowStep.getInput().get("instance");
        app.setInstances(instance);
        app.getLabels().put(MarathonAppConfigConstants.LABEL_ATA_EVENT_ID, appOpt.getAppId().toString());

        //dcosServiceId
        AppApi appApi = getBean(AppApi.class);
        AppInfoWithCredential appInfo = appApi.getAppInfoWithCredentialById(appOpt.getAppId())
                .ifNotSuccessThrowException().getData();
        EnvApi envApi = getBean(EnvApi.class);
        EnvDto envDto = envApi.get(appOpt.getEnvId()).ifNotSuccessThrowException().getData();
        String dcosServiceId = String.join("", "/", envDto.getCode(), appInfo.getDcosServiceId());

        //marathon
        ClusterConfigDto clusterConfigDto = envApi.getEnvConfiguration(appOpt.getEnvId())
                .ifNotSuccessThrowException().getData();
        String dcosEndpoint = String.join("", CommonConstants.PROTOCOL, clusterConfigDto.getDcosEndpoint(), CommonConstants.MARATHON_PORT);
        Marathon marathon = MarathonClient.getInstance(dcosEndpoint);

        //call & save result
        Result dcosResult = marathon.updateApp(dcosServiceId, app, true);
        appOpt.setAppDeployConfig(app.toString());
        appOpt.setAppDeployUuid(dcosResult.getDeploymentId());
        appOpt.setAppDeployVersion(dcosResult.getVersion());
        appOpt.setAppRunServiceId(dcosServiceId);
    }

    @Override
    public void stopApp(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {

    }

    @Override
    public void restartApp(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {

    }

    @Override
    public void scaleApp(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {

    }

    @Override
    public void rollback(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {

    }

    @Override
    public void deploy(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {

    }

    @Override
    public void healthCheck(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {

    }
}
