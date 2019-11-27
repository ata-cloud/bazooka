package net.atayun.bazooka.deploy.biz.v2.service.app.step.platform;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.youyu.common.exception.BizException;
import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppOptService;
import net.atayun.bazooka.pms.api.PmsCredentialsApi;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.dto.PmsCredentialsDto;
import net.atayun.bazooka.pms.api.feign.AppApi;
import net.atayun.bazooka.pms.api.param.PortMapping;
import net.atayun.bazooka.pms.api.param.VolumeMount;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.api.RmsClusterNodeApi;
import net.atayun.bazooka.rms.api.dto.ClusterConfigDto;
import net.atayun.bazooka.rms.api.dto.rsp.ClusterNodeRspDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = Platform.class, number = "2")
public class Platform4Node implements Platform {

    public static final Pattern COMPILE = Pattern.compile("-u\\s+(\\w+)\\s+-p\\s+(\\w+)");

    @Autowired
    private PmsCredentialsApi pmsCredentialsApi;

    private static final String DEPLOY_COMMAND = "sudo docker run --name=__CONTAINER_NAME__ __PORT_MAPPING__ __ENV__ __VOLUME__ -d __IMAGE_AND_TAG__";
    private static final String STOP_COMMAND = "sudo docker stop __CONTAINER_NAME__; docker rm __CONTAINER_NAME__";
    private static final String RESTART_COMMAND = "sudo docker restart __CONTAINER_NAME__";

    private String getVersion() {
        return LocalDateTime.now().toString();
    }

    private String getServiceId() {
        return UUID.randomUUID().toString().replace("-", "");
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

    @Override
    public void startApp(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        AppDeployConfigDto appDeployConfigDto = getBean(AppApi.class).getAppDeployConfigInfoById(appOpt.getDeployConfigId())
                .ifNotSuccessThrowException().getData();

        AppOptService appOptService = getBean(AppOptService.class);
        AppOpt lastAppOpt = appOptService.selectLastSuccessByAppIdAndEnv(appOpt.getAppId(), appOpt.getEnvId());

        String command = lastAppOpt.getAppDeployConfig();
        List<Long> clusterNodeIds = appDeployConfigDto.getClusterNodes();
        ssh(clusterNodeIds, command);

        appOpt.setAppDeployUuid(getUuid(clusterNodeIds));
        appOpt.setAppRunServiceId(getServiceId());
        appOpt.setAppDeployVersion(getVersion());
        appOpt.setAppDeployConfig(command);
    }

    @Override
    public void stopApp(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        stopApp(appOpt, true);
    }

    public void stopApp(AppOpt appOpt, boolean updateAppOpt) {
        AppOptService appOptService = getBean(AppOptService.class);
        AppOpt lastAppOpt = appOptService.selectLastSuccessByAppIdAndEnv(appOpt.getAppId(), appOpt.getEnvId());
        if (lastAppOpt == null) {
            return;
        }
        String containerName = lastAppOpt.getAppRunServiceId();
        String command = STOP_COMMAND.replace("__CONTAINER_NAME__", containerName);
        List<Long> cNodeIds = getNodeIds(lastAppOpt.getAppDeployUuid());
        ssh(cNodeIds, command);
        if (updateAppOpt) {
            appOpt.setAppDeployConfig(command);
            appOpt.setAppDeployUuid(lastAppOpt.getAppDeployUuid());
            appOpt.setAppDeployVersion(getVersion());
            appOpt.setAppRunServiceId(containerName);
        }
    }

    @Override
    public void restartApp(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        AppOpt lastAppOpt = getBean(AppOptService.class).selectLastSuccessByAppIdAndEnv(appOpt.getAppId(), appOpt.getEnvId());
        String containerName = lastAppOpt.getAppRunServiceId();
        String command = RESTART_COMMAND.replace("__CONTAINER_NAME__", containerName);
        List<Long> nodeIds = getNodeIds(lastAppOpt.getAppDeployUuid());
        ssh(nodeIds, command);
        appOpt.setAppDeployConfig(command);
        appOpt.setAppDeployUuid(lastAppOpt.getAppDeployUuid());
        appOpt.setAppDeployVersion(getVersion());
        appOpt.setAppRunServiceId(containerName);
    }

    @Override
    public void scaleApp(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        //...
    }

    @Override
    public void rollback(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        //关闭最新
        stopApp(appOpt, false);

        //复用历史
        List<Long> nodeIds = getNodeIds(appOpt.getAppDeployUuid());
        String command = appOpt.getAppDeployConfig();
        ssh(nodeIds, command);

        appOpt.setAppDeployVersion(getVersion());
    }

    @Override
    public void deploy(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        //关闭最新
        stopApp(appOpt, false);

        Map<String, Object> input = appOptFlowStep.getInput();
        AppDeployConfigDto appDeployConfigDto = getBean(AppApi.class).getAppDeployConfigInfoById(appOpt.getDeployConfigId())
                .ifNotSuccessThrowException().getData();

        String containerName = getServiceId();
        String image = (String) input.get("dockerImage");
        String port = "";
        List<PortMapping> portMappings = appDeployConfigDto.getPortMappings();
        if (CollectionUtils.isEmpty(portMappings)) {
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
        ssh(clusterNodeIds, command);

        appOpt.setAppDeployUuid(getUuid(clusterNodeIds));
        appOpt.setAppDeployVersion(getVersion());
        appOpt.setAppRunServiceId(containerName);
        appOpt.setAppDeployConfig(command);
    }

    @Override
    public void healthCheck(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        //...
    }

    private void ssh(List<Long> nodeIds, String command) {

        List<ClusterNodeRspDto> clusterNodes = getBean(RmsClusterNodeApi.class).getClusterNodeInfoByNodeIds(nodeIds)
                .ifNotSuccessThrowException().getData();
        for (ClusterNodeRspDto clusterNode : clusterNodes) {
            PmsCredentialsDto credentials;
            try {
                credentials = pmsCredentialsApi.getCredentialsDtoById(clusterNode.getCredentialId())
                        .ifNotSuccessThrowException().getData();
            } catch (Throwable throwable) {
                throw new BizException("", "", throwable);
            }
            try {
                JSch jSch = new JSch();
                Session session = jSch.getSession(credentials.getCredentialKey(), clusterNode.getIp(),
                        Integer.parseInt(clusterNode.getSshPort()));
                session.setPassword(credentials.getCredentialValue());
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();
                exec(session, command);
                session.disconnect();
            } catch (JSchException | IOException e) {
                throw new BizException("", "", e);
            }
        }
    }

    private void exec(Session session, String command) throws JSchException, IOException {
        ChannelExec exec = (ChannelExec) session.openChannel("exec");
        exec.setCommand(command);
        exec.setErrStream(System.err);
        exec.connect();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream(), StandardCharsets.UTF_8));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
        System.out.println(exec.getExitStatus());
        exec.disconnect();
    }

//    private void shell(Session session, String command) throws JSchException, IOException {
//        ChannelShell channel = (ChannelShell) session.openChannel("shell");
//        channel.connect();
//        OutputStream outputStream = channel.getOutputStream();
//        outputStream.write("sudo su".getBytes());
//        outputStream.write(command.getBytes());
//        outputStream.write("exit".getBytes());
////        outputStream.write("exit".getBytes());
//        outputStream.flush();
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(channel.getInputStream(), StandardCharsets.UTF_8));
//        String line;
//        while ((line = bufferedReader.readLine()) != null) {
//            System.out.println(line);
//        }
//        System.out.println(channel.getExitStatus());
//        channel.disconnect();
//    }
}
