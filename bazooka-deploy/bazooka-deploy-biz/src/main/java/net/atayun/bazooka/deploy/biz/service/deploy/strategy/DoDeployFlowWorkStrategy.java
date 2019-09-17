/*
 *    Copyright 2018-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.atayun.bazooka.deploy.biz.service.deploy.strategy;

import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.constant.CommonConstants;
import net.atayun.bazooka.base.dcos.DcosServerBean;
import net.atayun.bazooka.base.enums.deploy.AppOperationEventLogTypeEnum;
import net.atayun.bazooka.deploy.biz.constants.MarathonAppConfigConstants;
import net.atayun.bazooka.deploy.biz.dal.entity.app.AppOperationEventMarathonEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowImageEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.flow.DeployFlowMarathonEntity;
import net.atayun.bazooka.deploy.biz.enums.flow.DeployFlowEnum;
import net.atayun.bazooka.deploy.biz.log.LogConcat;
import net.atayun.bazooka.deploy.biz.service.app.AppOperationEventMarathonService;
import net.atayun.bazooka.deploy.biz.service.flow.DeployFlowImageService;
import net.atayun.bazooka.deploy.biz.service.flow.DeployFlowMarathonService;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.param.PortMapping;
import net.atayun.bazooka.pms.api.param.VolumeMount;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.dto.ClusterConfigDto;
import net.atayun.bazooka.rms.api.dto.EnvDto;
import mesosphere.dcos.client.DCOS;
import mesosphere.marathon.client.model.v2.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Ping
 * @date 2019-07-12
 */
@Component
@StrategyNum(
        superClass = AbstractDeployFlowWorkStrategy.class,
        number = DeployFlowEnum.FLOW_DO_DEPLOY,
        describe = "部署步骤")
public class DoDeployFlowWorkStrategy extends AbstractDeployFlowWorkStrategy {

    @Autowired
    private DcosServerBean dcosServerBean;

    @Autowired
    private EnvApi envApi;

    @Autowired
    private DeployFlowImageService deployFlowImageService;

    @Autowired
    private DeployFlowMarathonService deployFlowMarathonService;

    @Autowired
    private AppOperationEventMarathonService appOperationEventMarathonService;

    /**
     * 发布每个流程的具体实现
     *
     * @param workDetailPojo Flow所需所有数据实体
     */
    @Override
    public boolean doWorkDetail(WorkDetailPojo workDetailPojo) {
        final LogConcat logConcat = new LogConcat(">> 1. 发布操作相关信息");
        try {
            AppDeployConfigDto appDeployConfig = workDetailPojo.getAppDeployConfig();
            ClusterConfigDto clusterConfig = envApi.getEnvConfiguration(appDeployConfig.getEnvId())
                    .ifNotSuccessThrowException()
                    .getData();
            EnvDto envDto = envApi.get(appDeployConfig.getEnvId())
                    .ifNotSuccessThrowException()
                    .getData();
            Long deployFlowId = workDetailPojo.getDeployFlowEntity().getId();

            String dcosServiceId = "/" + envDto.getCode() + workDetailPojo.getAppInfo().getDcosServiceId();
            logConcat.concat("DC/OS Service Id: " + dcosServiceId);
            App app = getMarathonApp(workDetailPojo, appDeployConfig, clusterConfig, deployFlowId, dcosServiceId);

            String dcosEndpoint = CommonConstants.PROTOCOL + clusterConfig.getDcosEndpoint();
            logConcat.concat("DC/OS Endpoint: " + dcosEndpoint);
            logConcat.concat("服务配置:");
            logConcat.concat(app.toString());
            DCOS dcos = dcosServerBean.getInstance(dcosEndpoint);
            Result dcosResult = dcos.updateApp(dcosServiceId, app, true);

            String deploymentId = dcosResult.getDeploymentId();
            String deploymentVersion = dcosResult.getVersion();
            String marathonConfig = app.toString();
            saveEventMarathonInfo(workDetailPojo, deploymentId, deploymentVersion, marathonConfig);
            saveDeployFlowMarathonInfo(deployFlowId, dcosServiceId, deploymentId, deploymentVersion, marathonConfig);
            return true;
        } catch (Throwable throwable) {
            logConcat.concat(throwable);
            throw throwable;
        } finally {
            getAppOperationEventLog().save(workDetailPojo.getDeployEntity().getEventId(),
                    AppOperationEventLogTypeEnum.APP_DEPLOY_FLOW_DO_DEPLOY, 1, logConcat.get());
        }
    }

    @Override
    public void cancel(DeployFlowEntity deployFlowEntity) {

    }

    private void saveDeployFlowMarathonInfo(Long deployFlowId, String dcosServiceId, String deploymentId, String deploymentVersion, String marathonConfig) {
        DeployFlowMarathonEntity deployFlowMarathonEntity = new DeployFlowMarathonEntity();
        deployFlowMarathonEntity.setDeployFlowId(deployFlowId);
        deployFlowMarathonEntity.setMarathonDeploymentId(deploymentId);
        deployFlowMarathonEntity.setMarathonDeploymentVersion(deploymentVersion);
        deployFlowMarathonEntity.setMarathonServiceId(dcosServiceId);
        deployFlowMarathonEntity.setMarathonServiceConfig(marathonConfig);
        deployFlowMarathonService.insertEntity(deployFlowMarathonEntity);
    }

    private void saveEventMarathonInfo(WorkDetailPojo workDetailPojo, String deploymentId, String deploymentVersion, String marathonConfig) {
        AppOperationEventMarathonEntity appOperationEventMarathonEntity = new AppOperationEventMarathonEntity();
        appOperationEventMarathonEntity.setEventId(workDetailPojo.getDeployEntity().getEventId());
        appOperationEventMarathonEntity.setMarathonDeploymentId(deploymentId);
        appOperationEventMarathonEntity.setMarathonDeploymentVersion(deploymentVersion);
        appOperationEventMarathonEntity.setMarathonConfig(marathonConfig);
        appOperationEventMarathonService.insertEntity(appOperationEventMarathonEntity);
    }

    private App getMarathonApp(WorkDetailPojo workDetailPojo, AppDeployConfigDto appDeployConfig, ClusterConfigDto clusterConfig, Long deployFlowId, String dcosServiceId) {
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
        app.setContainer(getContainer(appDeployConfig, clusterConfig, deployFlowId));
        app.setLabels(getLabels(workDetailPojo));
        app.setHealthChecks(getHealthChecks(appDeployConfig));
        Map<String, Object> envVariable = Optional.ofNullable(appDeployConfig.getEnvironmentVariable()).orElseGet(HashMap::new);
        app.setEnv(envVariable);
        return app;
    }

    /**
     * Labels
     *
     * @param workDetailPojo workDetailPojo
     * @return Labels
     */
    private Map<String, String> getLabels(WorkDetailPojo workDetailPojo) {
        Map<String, String> map = new HashMap<>();
        map.put(MarathonAppConfigConstants.LABEL_ATA_EVENT_ID, workDetailPojo.getDeployEntity().getEventId().toString());
        List<PortMapping> portMappings = workDetailPojo.getAppDeployConfig().getPortMappings();
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

    /**
     * 初始化健康检查配置
     *
     * @param appDeployConfig 发布配置
     * @return HealthChecks
     */
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

    /**
     * 初始化容器配置
     *
     * @param appDeployConfig 发布配置
     * @param clusterConfig   环境集群配置
     * @param deployFlowId    发布流Id
     * @return Container
     */
    private Container getContainer(AppDeployConfigDto appDeployConfig, ClusterConfigDto clusterConfig, Long deployFlowId) {
        DeployFlowImageEntity deployFlowImageEntity = deployFlowImageService.selectByDeployFlowId(deployFlowId - 1);
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
        docker.setImage(clusterConfig.getDockerHubUrl() + "/" +
                deployFlowImageEntity.getDockerImageName() + ":" +
                deployFlowImageEntity.getDockerImageTag());
        docker.setForcePullImage(false);
        docker.setPrivileged(false);
        container.setDocker(docker);
        return container;
    }

    /**
     * 初始化网络配置
     *
     * @return Networks
     */
    private List<Network> getNetworks() {
        Network network = new Network();
        network.setMode(MarathonAppConfigConstants.NETWORK_MODE_BRIDGE);
        return Collections.singletonList(network);
    }
}
