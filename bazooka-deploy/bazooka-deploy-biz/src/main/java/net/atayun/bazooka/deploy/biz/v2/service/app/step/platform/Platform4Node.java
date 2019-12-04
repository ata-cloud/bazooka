package net.atayun.bazooka.deploy.biz.v2.service.app.step.platform;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.youyu.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.enums.AppOptEnum;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppOptService;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.deploymode.ICheckNodeResource;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.log.StepLogBuilder;
import net.atayun.bazooka.deploy.biz.v2.util.MessageDesensitizationUtil;
import net.atayun.bazooka.pms.api.PmsCredentialsApi;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.dto.PmsCredentialsDto;
import net.atayun.bazooka.pms.api.feign.AppApi;
import net.atayun.bazooka.pms.api.param.PortMapping;
import net.atayun.bazooka.pms.api.param.VolumeMount;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.api.RmsClusterNodeApi;
import net.atayun.bazooka.rms.api.api.RmsContainerApi;
import net.atayun.bazooka.rms.api.dto.ClusterConfigDto;
import net.atayun.bazooka.rms.api.dto.EnvDto;
import net.atayun.bazooka.rms.api.dto.rsp.ClusterNodeRspDto;
import net.atayun.bazooka.rms.api.enums.ClusterAppServiceStatusEnum;
import net.atayun.bazooka.rms.api.param.NodeContainerParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;
import static net.atayun.bazooka.deploy.biz.v2.constant.DeployResultCodeConstants.ACCESS_NODE_ERR_CODE;
import static net.atayun.bazooka.deploy.biz.v2.constant.DeployResultCodeConstants.RESOURCE_ERR_CODE;

/**
 * @author Ping
 */
@Slf4j
@Component
@StrategyNum(superClass = Platform.class, number = "2")
public class Platform4Node implements Platform, ICheckNodeResource {

    private final Pattern pattern = Pattern.compile("--cpus\\s([0-9]+\\.?[0-9]*)\\s-m\\s(\\d+)M");

    private final Pattern cpuPattern = Pattern.compile("--cpus\\s([0-9]+\\.?[0-9]*)\\s");

    private final Pattern memPattern = Pattern.compile("-m\\s(\\d+)M");

    private final Pattern portPattern = Pattern.compile("-p\\s(\\d+:\\d+)\\s+");

    private final Pattern volumePattern = Pattern.compile("-v\\s(\\S+?:\\S+?)\\s+");

    private final Pattern envVariablePattern = Pattern.compile("-e\\s(\\S+?=\\S+?)\\s+");

    private final Pattern imagePattern = Pattern.compile("-d\\s+(\\S+)\\s+");

    private final Pattern namePattern = Pattern.compile("--name=(\\S+)\\s");

    @Autowired
    private PmsCredentialsApi pmsCredentialsApi;

    private static final String DEPLOY_COMMAND = "sudo docker run " +
            "--name=__CONTAINER_NAME__ " +
            "__PORT_MAPPING__ " +
            "__ENV__ " +
            "__VOLUME__ " +
            "--cpus __CPUS__ " +
            "-m __MEMORY__M " +
            "-d " +
            "__IMAGE_AND_TAG__ ";
    private static final String STOP_COMMAND = "sudo docker stop __CONTAINER_NAME__ && sudo docker rm __CONTAINER_NAME__";
    private static final String RESTART_COMMAND = "sudo docker restart __CONTAINER_NAME__";

    @Override
    public void startApp(AppOpt appOpt, AppOptFlowStep appOptFlowStep, StepLogBuilder logBuilder) {
        AppDeployConfigDto appDeployConfigDto = getBean(AppApi.class).getAppDeployConfigInfoById(appOpt.getDeployConfigId())
                .ifNotSuccessThrowException().getData();

        AppOpt lastAppOpt = getBean(AppOptService.class).selectLastSuccessByAppIdAndEnv(appOpt.getAppId(), appOpt.getEnvId());

        String command = lastAppOpt.getAppDeployConfig();
        List<Long> clusterNodeIds = appDeployConfigDto.getClusterNodes();
        checkResource(command, clusterNodeIds, logBuilder);
        List<NodeContainerParam> params = ssh(appOpt, clusterNodeIds, command, logBuilder);

        appOpt.setAppDeployUuid(getUuid(clusterNodeIds));
        appOpt.setAppRunServiceId(getServiceId());
        appOpt.setAppDeployVersion(getVersion());
        appOpt.setAppDeployConfig(command);
        appOpt.setDockerImageTag(lastAppOpt.getDockerImageTag());
        updateContainer(appOpt, params);
    }

    @Override
    public void stopApp(AppOpt appOpt, AppOptFlowStep appOptFlowStep, StepLogBuilder logBuilder) {
        stopApp(appOpt, true, logBuilder);
    }

    private void stopApp(AppOpt appOpt, boolean isStopOpt, StepLogBuilder logBuilder) {
        AppOpt lastAppOpt = getBean(AppOptService.class).selectLastSuccessByAppIdAndEnv(appOpt.getAppId(), appOpt.getEnvId());
        if (lastAppOpt == null) {
            return;
        }

        String containerName = lastAppOpt.getAppRunServiceId();
        String command = STOP_COMMAND.replace("__CONTAINER_NAME__", containerName);
        List<Long> cNodeIds = getNodeIds(lastAppOpt.getAppDeployUuid());
        try {
            ssh(appOpt, cNodeIds, command, logBuilder);
        } catch (Throwable throwable) {
            if (isStopOpt) {
                throw throwable;
            }
        }

        if (isStopOpt) {
            appOpt.setAppDeployConfig(command);
            appOpt.setAppDeployUuid(lastAppOpt.getAppDeployUuid());
            appOpt.setAppDeployVersion(getVersion());
            appOpt.setAppRunServiceId(containerName);
            appOpt.setDockerImageTag(lastAppOpt.getDockerImageTag());
            updateContainer(appOpt, new ArrayList<>());
        }
    }

    @Override
    public void restartApp(AppOpt appOpt, AppOptFlowStep appOptFlowStep, StepLogBuilder logBuilder) {
        AppOpt lastAppOpt = getBean(AppOptService.class).selectLastSuccessByAppIdAndEnv(appOpt.getAppId(), appOpt.getEnvId());

        String containerName = lastAppOpt.getAppRunServiceId();
        String command = RESTART_COMMAND.replace("__CONTAINER_NAME__", containerName);
        List<Long> nodeIds = getNodeIds(lastAppOpt.getAppDeployUuid());
        checkResource(command, nodeIds, logBuilder);
        List<NodeContainerParam> params = ssh(appOpt, nodeIds, command, logBuilder);

        appOpt.setAppDeployConfig(command);
        appOpt.setAppDeployUuid(lastAppOpt.getAppDeployUuid());
        appOpt.setAppDeployVersion(getVersion());
        appOpt.setDockerImageTag(lastAppOpt.getDockerImageTag());
        appOpt.setAppRunServiceId(containerName);
        updateContainer(appOpt, params);
    }

    @Override
    public void scaleApp(AppOpt appOpt, AppOptFlowStep appOptFlowStep, StepLogBuilder logBuilder) {
        //...
    }

    @Override
    public void rollback(AppOpt appOpt, AppOptFlowStep appOptFlowStep, StepLogBuilder logBuilder) {
        //关闭最新
        stopApp(appOpt, false, logBuilder);

        //复用历史
        List<Long> nodeIds = getNodeIds(appOpt.getAppDeployUuid());
        String command = appOpt.getAppDeployConfig();
        checkResource(command, nodeIds, logBuilder);
        List<NodeContainerParam> params = ssh(appOpt, nodeIds, command, logBuilder);

        appOpt.setAppDeployVersion(getVersion());
        updateContainer(appOpt, params);
    }

    @Override
    public void deploy(AppOpt appOpt, AppOptFlowStep appOptFlowStep, StepLogBuilder logBuilder) {
        //关闭最新
        stopApp(appOpt, false, logBuilder);

        Map<String, Object> input = appOptFlowStep.getInput();
        AppDeployConfigDto appDeployConfigDto = getBean(AppApi.class).getAppDeployConfigInfoById(appOpt.getDeployConfigId())
                .ifNotSuccessThrowException().getData();

        String containerName = getServiceId();
        String image = (String) input.get("dockerImage");
        String port = "";
        List<PortMapping> portMappings = appDeployConfigDto.getPortMappings();
        if (!CollectionUtils.isEmpty(portMappings)) {
            port = portMappings.stream()
                    .map(portMapping -> " -p " + portMapping.getServicePort() + ":" + portMapping.getContainerPort())
                    .collect(Collectors.joining(" "));
        }
        Map<String, Object> environmentVariable = appDeployConfigDto.getEnvironmentVariable();
        StringBuilder env = new StringBuilder();
        if (!CollectionUtils.isEmpty(environmentVariable)) {
            environmentVariable.forEach((k, v) -> env.append(" -e ").append(k).append("=").append("\"").append(v).append("\" "));
        }
        List<VolumeMount> volumes = appDeployConfigDto.getVolumes();
        String volume = "";
        if (!CollectionUtils.isEmpty(volumes)) {
            volume = volumes.stream()
                    .map(volumeMount -> " -v " + volumeMount.getHostPath() + ":" + volumeMount.getContainerPath())
                    .collect(Collectors.joining(" "));
        }
        String command = DEPLOY_COMMAND.replace("__CONTAINER_NAME__", containerName)
                .replace("__PORT_MAPPING__", port)
                .replace("__ENV__", env.toString())
                .replace("__VOLUME__", volume)
                .replace("__CPUS__", appDeployConfigDto.getCpus().toString())
                .replace("__MEMORY__", appDeployConfigDto.getMemory().toString())
                .replace("__IMAGE_AND_TAG__", image);
        ClusterConfigDto clusterConfigDto = getBean(EnvApi.class).getEnvConfiguration(appOpt.getEnvId()).ifNotSuccessThrowException().getData();
        if (clusterConfigDto.getDockerHubCredentialId() != null) {
            PmsCredentialsDto credentials = pmsCredentialsApi.getCredentialsDtoById(clusterConfigDto.getDockerHubCredentialId())
                    .ifNotSuccessThrowException().getData();
            String host = image.split("/")[0];
            String login = "sudo docker login" +
                    " -u " + credentials.getCredentialKey() +
                    " -p " + credentials.getCredentialValue() +
                    " " + host;
            String logout = "sudo docker logout " + host;
            command = login + " && " + command + " && " + logout;
        }
        List<Long> clusterNodeIds = appDeployConfigDto.getClusterNodes();
        checkResource(command, clusterNodeIds, logBuilder);
        List<NodeContainerParam> params = ssh(appOpt, clusterNodeIds, command, logBuilder);

        appOpt.setAppDeployUuid(getUuid(clusterNodeIds));
        appOpt.setAppDeployVersion(getVersion());
        appOpt.setAppRunServiceId(containerName);
        appOpt.setAppDeployConfig(command);
        updateContainer(appOpt, params);
    }

    @Override
    public void healthCheck(AppOpt appOpt, AppOptFlowStep appOptFlowStep, StepLogBuilder logBuilder) {
        //...
    }

    private List<NodeContainerParam> ssh(AppOpt appOpt, List<Long> nodeIds, String command, StepLogBuilder logBuilder) {
        EnvDto env = getBean(EnvApi.class).get(appOpt.getEnvId()).ifNotSuccessThrowException().getData();
        List<ClusterNodeRspDto> clusterNodes = getBean(RmsClusterNodeApi.class).getClusterNodeInfoByNodeIds(nodeIds)
                .ifNotSuccessThrowException().getData();
        List<NodeContainerParam> params = new ArrayList<>();
        for (ClusterNodeRspDto clusterNode : clusterNodes) {
            String ip = clusterNode.getIp();
            logBuilder.append("节点: " + ip + ", CMD: " + MessageDesensitizationUtil.replaceDockerCmd(command));
            //凭证可能异常
            PmsCredentialsDto credentials = pmsCredentialsApi.getCredentialsDtoById(clusterNode.getCredentialId())
                    .ifNotSuccessThrowException().getData();
            try {
                JSch jSch = new JSch();
                Session session = jSch.getSession(credentials.getCredentialKey(), ip,
                        Integer.parseInt(clusterNode.getSshPort()));
                session.setPassword(credentials.getCredentialValue());
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();
                exec(session, command, logBuilder);
                session.disconnect();
                if (appOpt.getOpt() != AppOptEnum.STOP) {
                    NodeContainerParam nodeContainerParam = buildNodeContainerParam(env.getClusterId(), env.getId(),
                            clusterNode.getId(), appOpt.getAppId(), clusterNode.getIp(), command);
                    params.add(nodeContainerParam);
                }
            } catch (JSchException | IOException e) {
                throw new BizException(ACCESS_NODE_ERR_CODE, "节点访问异常[" + ip + "]", e);
            }
        }
        return params;
    }

    private NodeContainerParam buildNodeContainerParam(Long clusterId, Long envId, Long nodeId, Long appId,
                                                       String ip, String command) {
        NodeContainerParam nodeContainerParam = new NodeContainerParam();
        nodeContainerParam.setClusterId(clusterId);
        nodeContainerParam.setEnvId(envId);
        nodeContainerParam.setNodeId(nodeId);
        nodeContainerParam.setAppId(appId);
        nodeContainerParam.setIp(ip);
        nodeContainerParam.setContainerName(parseName(command));
        nodeContainerParam.setContainerStatus(ClusterAppServiceStatusEnum.RUNNING);
        nodeContainerParam.setContainerImage(parseImage(command));
        nodeContainerParam.setCpu(parseCpu(command));
        nodeContainerParam.setMemory(parseMem(command));
        nodeContainerParam.setDisk(BigDecimal.ZERO);
        nodeContainerParam.setPortMapping(parsePortMapping(command));
        nodeContainerParam.setEnvVariable(parseEnvVariable(command));
        nodeContainerParam.setVolume(parseVolume(command));
        return nodeContainerParam;
    }

    private void exec(Session session, String command, StepLogBuilder logBuilder) throws JSchException, IOException {
        ChannelExec exec = (ChannelExec) session.openChannel("exec");
        exec.setCommand(command);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        exec.setErrStream(byteArrayOutputStream);
        exec.connect();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                logBuilder.append(line);
            }
        }
        exec.disconnect();
        String err = byteArrayOutputStream.toString();
        if (StringUtils.hasText(err)) {
            logBuilder.append("执行命令异常: " + err);
        }
        int exitStatus = exec.getExitStatus();
        logBuilder.append("CmdCode: " + exitStatus);
        if (exitStatus > 0) {
            throw new BizException(RESOURCE_ERR_CODE, "执行命令异常: " + err);
        }
    }

    private String getVersion() {
        return LocalDateTime.now().toString();
    }

    private String getServiceId() {
        return UUID.randomUUID().toString().replace("-", "_");
    }

    private String getUuid(List<Long> clusterNodeIds) {
        if (CollectionUtils.isEmpty(clusterNodeIds)) {
            return "";
        }
        return clusterNodeIds.stream().map(Object::toString).collect(Collectors.joining(","));
    }

    private List<Long> getNodeIds(String uuid) {
        if (StringUtils.isEmpty(uuid)) {
            return new ArrayList<>();
        }
        String[] nodeIdsStr = uuid.split(",");
        return Arrays.stream(nodeIdsStr).map(Long::parseLong).collect(Collectors.toList());
    }

    private void checkResource(String command, List<Long> clusterNodeIds, StepLogBuilder logBuilder) {
        Matcher matcher = pattern.matcher(command);
        if (matcher.find()) {
            String cpuStr = matcher.group(1);
            if (StringUtils.isEmpty(cpuStr)) {
                throw new BizException(RESOURCE_ERR_CODE, "cpu数据配置异常");
            }
            String memStr = matcher.group(2);
            if (StringUtils.isEmpty(memStr)) {
                throw new BizException(RESOURCE_ERR_CODE, "内存数据配置异常");
            }
            BigDecimal cpu = new BigDecimal(cpuStr);
            BigDecimal mem = new BigDecimal(memStr);
            checkNodeResource(clusterNodeIds, cpu, mem, logBuilder);
            return;
        }
        throw new BizException(RESOURCE_ERR_CODE, "未设置cpu和内存");
    }

    private void updateContainer(AppOpt appOpt, List<NodeContainerParam> params) {
        getBean(RmsContainerApi.class).insert(appOpt.getAppId(), appOpt.getOpt(), params);
    }

    private List<String> parsePortMapping(String command) {
        Matcher matcher = portPattern.matcher(command);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            String port = matcher.group(1);
            if (StringUtils.hasText(port)) {
                list.add(port);
            }
        }
        return list;
    }

    private List<String> parseEnvVariable(String command) {
        Matcher matcher = envVariablePattern.matcher(command);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            String envVariable = matcher.group(1);
            if (StringUtils.hasText(envVariable)) {
                list.add(envVariable);
            }
        }
        return list;
    }

    private List<String> parseVolume(String command) {
        Matcher matcher = volumePattern.matcher(command);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            String volume = matcher.group(1);
            if (StringUtils.hasText(volume)) {
                list.add(volume);
            }
        }
        return list;
    }

    private BigDecimal parseCpu(String command) {
        Matcher matcher = cpuPattern.matcher(command);
        if (matcher.find()) {
            String cpu = matcher.group(1);
            if (StringUtils.hasText(cpu)) {
                return new BigDecimal(cpu);
            }
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal parseMem(String command) {
        Matcher matcher = memPattern.matcher(command);
        if (matcher.find()) {
            String mem = matcher.group(1);
            if (StringUtils.hasText(mem)) {
                return new BigDecimal(mem);
            }
        }
        return BigDecimal.ZERO;
    }

    private String parseImage(String command) {
        Matcher matcher = imagePattern.matcher(command);
        if (matcher.find()) {
            String image = matcher.group(1);
            if (StringUtils.hasText(image)) {
                return image;
            }
        }
        return "";
    }

    private String parseName(String command) {
        Matcher matcher = namePattern.matcher(command);
        if (matcher.find()) {
            String name = matcher.group(1);
            if (StringUtils.hasText(name)) {
                return name;
            }
        }
        return "";
    }
}
