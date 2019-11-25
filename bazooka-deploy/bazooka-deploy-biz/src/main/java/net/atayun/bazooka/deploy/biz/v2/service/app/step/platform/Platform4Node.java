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
import net.atayun.bazooka.rms.api.api.RmsClusterNodeApi;
import net.atayun.bazooka.rms.api.dto.rsp.ClusterNodeRspDto;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = Platform.class, number = "SINGLENODE")
public class Platform4Node implements Platform {

    @Override
    public void startApp(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        AppDeployConfigDto appDeployConfigDto = getBean(AppApi.class).getAppDeployConfigInfoById(appOpt.getDeployConfigId())
                .ifNotSuccessThrowException().getData();
        List<Long> clusterNodeIds = appDeployConfigDto.getClusterNodes();

        List<ClusterNodeRspDto> clusterNodes = getBean(RmsClusterNodeApi.class).getClusterNodeInfoByNodeIds(clusterNodeIds)
                .ifNotSuccessThrowException().getData();

        AppOptService appOptService = getBean(AppOptService.class);
        AppOpt lastAppOpt = appOptService.selectLastSuccessByAppIdAndEnv(appOpt.getAppId(), appOpt.getEnvId());
        String command = lastAppOpt.getAppDeployConfig();

        PmsCredentialsApi pmsCredentialsApi = getBean(PmsCredentialsApi.class);
        for (ClusterNodeRspDto clusterNode : clusterNodes) {
            PmsCredentialsDto credentials;
            try {
                credentials = pmsCredentialsApi.getCredentialsDtoById(clusterNode.getCredentialId())
                        .ifNotSuccessThrowException().getData();
            } catch (Throwable throwable) {
                throw new BizException("", "", throwable);
            }
            try {
                ssh(clusterNode.getIp(), credentials.getCredentialKey(), credentials.getCredentialValue(), command);
            } catch (JSchException | IOException e) {
                throw new BizException("", "", e);
            }
        }
        appOpt.setAppDeployConfig(command);
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
        //...
    }

    private void ssh(String ip, String username, String password, String command) throws JSchException, IOException {
        JSch jSch = new JSch();
        Session session = jSch.getSession(username, ip);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        exec(session, command);
        session.disconnect();
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
}
