package net.atayun.bazooka.deploy.biz.v2.service.app.opt.remark;

import mesosphere.client.common.ModelUtils;
import mesosphere.marathon.client.model.v2.App;
import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.deploy.biz.v2.constant.DeployConstants;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppOptService;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.dto.EnvDto;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = AppOptType.class, number = "SCALE")
public class AppOptType4Scale implements AppOptType {

    @Override
    public String remark(AppOpt appOpt) {
        EnvDto env = getBean(EnvApi.class).get(appOpt.getEnvId()).ifNotSuccessThrowException().getData();
        if (Objects.equals(env.getClusterType(), DeployConstants.NODE_CODE)) {
            return "";
        }

        //Marathon
        AppOpt last = getBean(AppOptService.class).selectLastSuccessByAppIdAndEnv(appOpt.getAppId(), appOpt.getEnvId());
        App app = ModelUtils.GSON.fromJson(last.getAppDeployConfig(), App.class);
        //instance
        Integer instance = Optional.ofNullable(appOpt.getInstance()).orElse(0);
        String instanceChange = "";
        if (instance.compareTo(app.getInstances()) != 0) {
            instanceChange = "实例个数: " + app.getInstances() + " -> " + instance;
        }

        //cpu
        Double cpu = Optional.ofNullable(appOpt.getCpu()).orElse(0D);
        String cpuChange = "";
        if (cpu.compareTo(app.getCpus()) != 0) {
            cpuChange = "CPU Shares: " + app.getCpus() + " -> " + cpu;
        }

        //mem
        Double mem = Optional.ofNullable(appOpt.getMemory()).orElse(0D);
        String memChange = "";
        if (mem.compareTo(app.getMem()) != 0) {
            memChange = "内存大小: " + app.getMem() + " -> " + mem;
        }

        return Stream.of(instanceChange, cpuChange, memChange)
                .filter(StringUtils::hasText)
                .collect(Collectors.joining(", "));
    }
}