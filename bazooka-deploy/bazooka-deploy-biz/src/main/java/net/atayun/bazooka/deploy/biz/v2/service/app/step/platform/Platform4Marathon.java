package net.atayun.bazooka.deploy.biz.v2.service.app.step.platform;

import mesosphere.client.common.ModelUtils;
import mesosphere.marathon.client.Marathon;
import mesosphere.marathon.client.MarathonClient;
import mesosphere.marathon.client.model.v2.*;
import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.constant.CommonConstants;
import net.atayun.bazooka.deploy.biz.constants.MarathonAppConfigConstants;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppOptService;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.dto.AppInfoWithCredential;
import net.atayun.bazooka.pms.api.feign.AppApi;
import net.atayun.bazooka.pms.api.param.PortMapping;
import net.atayun.bazooka.pms.api.param.VolumeMount;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.dto.ClusterConfigDto;
import net.atayun.bazooka.rms.api.dto.EnvDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = Platform.class, number = "0")
public class Platform4Marathon implements Platform {

    @Autowired
    private AppApi appApi;

    @Autowired
    private EnvApi envApi;

    void lifeCycle(AppOpt appOpt, Consumer<App> consumer) {
        AppOptService appOptService = getBean(AppOptService.class);

        //app
        AppOpt lastAppOpt = appOptService.selectLastSuccessByAppIdAndEnv(appOpt.getAppId(), appOpt.getEnvId());
        String deployConfig = lastAppOpt.getAppDeployConfig();
        App app = ModelUtils.GSON.fromJson(deployConfig, App.class);
        consumer.accept(app);
        app.getLabels().put(MarathonAppConfigConstants.LABEL_ATA_EVENT_ID, appOpt.getAppId().toString());

        //dcosServiceId
        String dcosServiceId = getDcosServiceId(appOpt);

        //marathon
        Marathon marathon = getMarathon(appOpt);

        //call & save result
        Result dcosResult = marathon.updateApp(dcosServiceId, app, true);
        saveResult(appOpt, dcosServiceId, app, dcosResult);
    }

    @Override
    public void startApp(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        Consumer<App> consumer = app -> {
            Integer instance = (Integer) appOptFlowStep.getInput().get("instance");
            app.setInstances(instance);
        };
        lifeCycle(appOpt, consumer);
    }

    @Override
    public void stopApp(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        Consumer<App> consumer = app -> app.setInstances(0);
        lifeCycle(appOpt, consumer);
    }

    @Override
    public void restartApp(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        lifeCycle(appOpt, app -> {
        });
    }

    @Override
    public void scaleApp(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        Consumer<App> consumer = app -> {
            Integer instance = (Integer) appOptFlowStep.getInput().get("instance");
            Double cpu = (Double) appOptFlowStep.getInput().get("cpu");
            Double mem = (Double) appOptFlowStep.getInput().get("memory");
            app.setInstances(instance);
            app.setCpus(cpu);
            app.setMem(mem);
        };
        lifeCycle(appOpt, consumer);
    }

    @Override
    public void rollback(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        App app = ModelUtils.GSON.fromJson(appOpt.getAppDeployConfig(), App.class);
        app.getLabels().put(MarathonAppConfigConstants.LABEL_ATA_EVENT_ID, appOpt.getAppId().toString());

        Marathon marathon = getMarathon(appOpt);
        String dcosServiceId = getDcosServiceId(appOpt);

        //call & save result
        Result dcosResult = marathon.updateApp(dcosServiceId, app, true);
        saveResult(appOpt, dcosServiceId, app, dcosResult);
    }

    @Override
    public void deploy(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        AppDeployConfigDto appDeployConfig = appApi.getAppDeployConfigInfoById(appOpt.getDeployConfigId())
                .ifNotSuccessThrowException().getData();
        ClusterConfigDto clusterConfig = envApi.getEnvConfiguration(appOpt.getEnvId())
                .ifNotSuccessThrowException().getData();

        //dcosServiceId
        String dcosServiceId = getDcosServiceId(appOpt);

        App app = new App();
        app.setId(dcosServiceId);
        app.setCpus(appDeployConfig.getCpus());
        app.setMem(appDeployConfig.getMemory().doubleValue());
        app.setDisk(appDeployConfig.getDisk().doubleValue());
        app.setInstances(appDeployConfig.getInstance());
        app.setNetworks(getNetworks());
        if (StringUtils.hasText(appDeployConfig.getStartCommand())) {
            app.setCmd(appDeployConfig.getStartCommand());
        }
        app.setContainer(getContainer(appDeployConfig, clusterConfig, appOptFlowStep.getInput()));
        app.setLabels(getLabels(appOpt, appDeployConfig));
        app.setHealthChecks(getHealthChecks(appDeployConfig));
        Map<String, Object> envVariable = Optional.ofNullable(appDeployConfig.getEnvironmentVariable()).orElseGet(HashMap::new);
        app.setEnv(envVariable);

        Marathon marathon = getMarathon(appOpt);

        Result dcosResult = marathon.updateApp(dcosServiceId, app, true);
        saveResult(appOpt, dcosServiceId, app, dcosResult);
    }

    @Override
    public void healthCheck(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        //...
    }

    private String getDcosServiceId(AppOpt appOpt) {
        AppInfoWithCredential appInfo = appApi.getAppInfoWithCredentialById(appOpt.getAppId())
                .ifNotSuccessThrowException().getData();
        EnvDto envDto = envApi.get(appOpt.getEnvId()).ifNotSuccessThrowException().getData();
        return String.join("", "/", envDto.getCode(), appInfo.getDcosServiceId());
    }

    private Marathon getMarathon(AppOpt appOpt) {
        ClusterConfigDto clusterConfigDto = envApi.getEnvConfiguration(appOpt.getEnvId())
                .ifNotSuccessThrowException().getData();
        String dcosEndpoint = String.join("", CommonConstants.PROTOCOL, clusterConfigDto.getDcosEndpoint(), CommonConstants.MARATHON_PORT);
        return MarathonClient.getInstance(dcosEndpoint);
    }

    private Map<String, String> getLabels(AppOpt appOpt, AppDeployConfigDto appDeployConfig) {
        Map<String, String> map = new HashMap<>();
        map.put(MarathonAppConfigConstants.LABEL_ATA_EVENT_ID, appOpt.getId().toString());
        List<PortMapping> portMappings = appDeployConfig.getPortMappings();
        if (CollectionUtils.isEmpty(portMappings)) {
            return map;
        }
        List<Integer> exportPortIndexList = new ArrayList<>();
        for (int i = 0; i < portMappings.size(); i++) {
            PortMapping portMapping = portMappings.get(i);
            Integer servicePort = portMapping.getServicePort();
            if (servicePort != null) {
                exportPortIndexList.add(i);
            }
        }
        if (!CollectionUtils.isEmpty(exportPortIndexList)) {
            map.put("HAPROXY_GROUP", "external");
        }
        if (exportPortIndexList.size() != portMappings.size()) {
            exportPortIndexList.forEach(exportPortIndex -> {
                map.put("HAPROXY_" + exportPortIndex + "_FRONTEND_HEAD", "");
                map.put("HAPROXY_" + exportPortIndex + "_BACKEND_HEAD", "");
                map.put("HAPROXY_" + exportPortIndex + "_FRONTEND_BACKEND_GLUE", "");
            });
        }
        return map;
    }

    private List<HealthCheck> getHealthChecks(AppDeployConfigDto appDeployConfig) {
        List<net.atayun.bazooka.pms.api.param.HealthCheck> healthChecks = appDeployConfig.getHealthChecks();
        if (CollectionUtils.isEmpty(healthChecks)) {
            return new ArrayList<>();
        }
        return healthChecks.stream()
                .map(healthCheck -> {
                    HealthCheck hc = new HealthCheck();
                    hc.setGracePeriodSeconds(healthCheck.getGracePeriodSeconds());
                    hc.setIntervalSeconds(healthCheck.getIntervalSeconds());
                    hc.setMaxConsecutiveFailures(healthCheck.getMaxConsecutiveFailures());
                    hc.setPortIndex(healthCheck.getPortIndex());
                    hc.setTimeoutSeconds(healthCheck.getTimeoutSeconds());
                    if (healthCheck.getProtocol() != null) {
                        hc.setProtocol(healthCheck.getProtocol().name());
                    }
                    if (healthCheck.getIpProtocol() != null) {
                        hc.setIpProtocol(healthCheck.getIpProtocol().name());
                    }
                    if (StringUtils.hasText(healthCheck.getPath())) {
                        hc.setPath(healthCheck.getPath());
                    }
                    return hc;
                })
                .collect(Collectors.toList());
    }

    private Container getContainer(AppDeployConfigDto appDeployConfig, ClusterConfigDto clusterConfig, Map<String, Object> input) {
        Container container = new Container();
        @NotNull List<PortMapping> portMappingList = appDeployConfig.getPortMappings();
        List<Port> portMappings = new ArrayList<>();
        if (!CollectionUtils.isEmpty(portMappingList)) {
            portMappings = portMappingList.stream()
                    .map(portMapping -> {
                        Port port = new Port();
                        port.setContainerPort(portMapping.getContainerPort());
                        port.setServicePort(portMapping.getServicePort());
                        return port;
                    }).collect(Collectors.toList());
        }
        container.setPortMappings(portMappings);
        container.setType(MarathonAppConfigConstants.CONTAINER_TYPE_DOCKER);
        List<VolumeMount> volumes = appDeployConfig.getVolumes();
        List<LocalVolume> localVolumes = new ArrayList<>();
        if (!CollectionUtils.isEmpty(volumes)) {
            localVolumes = volumes.stream()
                    .map(volumeMount -> {
                        LocalVolume volume = new LocalVolume();
                        volume.setHostPath(volumeMount.getHostPath());
                        volume.setContainerPath(volumeMount.getContainerPath());
                        volume.setMode(volumeMount.getMode().getValue());
                        return volume;
                    })
                    .collect(Collectors.toList());
        }
        container.setVolumes(Collections.unmodifiableCollection(localVolumes));
        Docker docker = new Docker();
        String dockerImageName = (String) input.get("dockerImageName");
        String dockerImageTag = (String) input.get("dockerImageTag");
        docker.setImage(clusterConfig.getDockerHubUrl() + "/" +
                dockerImageName + ":" +
                dockerImageTag);
        docker.setForcePullImage(false);
        docker.setPrivileged(false);
        container.setDocker(docker);
        return container;
    }

    private List<Network> getNetworks() {
        Network network = new Network();
        network.setMode(MarathonAppConfigConstants.NETWORK_MODE_BRIDGE);
        return Collections.singletonList(network);
    }

    private void saveResult(AppOpt appOpt, String dcosServiceId, App app, Result dcosResult) {
        appOpt.setAppDeployConfig(app.toString());
        appOpt.setAppDeployUuid(dcosResult.getDeploymentId());
        appOpt.setAppDeployVersion(dcosResult.getVersion());
        appOpt.setAppRunServiceId(dcosServiceId);
    }
}
