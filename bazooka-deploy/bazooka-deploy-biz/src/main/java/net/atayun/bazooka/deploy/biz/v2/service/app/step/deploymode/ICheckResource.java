package net.atayun.bazooka.deploy.biz.v2.service.app.step.deploymode;

import com.youyu.common.exception.BizException;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.log.StepLogBuilder;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.feign.AppApi;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.dto.EnvResourceDto;

import java.math.BigDecimal;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;
import static net.atayun.bazooka.deploy.biz.v2.constant.DeployResultCodeConstants.*;

/**
 * @author Ping
 */
public interface ICheckResource {

    default void checkPlatformResource(AppOpt appOpt, StepLogBuilder stepLogBuilder) {
        stepLogBuilder.append("检查资源");
        AppDeployConfigDto deployConfig = getBean(AppApi.class).getAppDeployConfigInfoById(appOpt.getDeployConfigId())
                .ifNotSuccessThrowException().getData();
        String configName = deployConfig.getConfigName();
        Integer instance = deployConfig.getInstance();
        if (instance == 0) {
            throw new BizException(APP_INSTANCE_ZERO, String.format("发布配置[%s]实例数为0", configName));
        }
        stepLogBuilder.append("实例个数: " + instance);

        EnvResourceDto envResource = getBean(EnvApi.class).getEnvAvailableResource(deployConfig.getEnvId())
                .ifNotSuccessThrowException().getData();

        //cpus
        double totalShares = deployConfig.getCpus() * instance;
        boolean sharesIsAdequacy = envResource.getCpus()
                .subtract(new BigDecimal(totalShares))
                .compareTo(BigDecimal.ZERO) >= 0;
        if (!sharesIsAdequacy) {
            String msg = String.format("发布配置[%s]当前发布操作CPU资源不足, 请确认当前环境下是否有足够的CPU shares", configName);
            throw new BizException(ILLEGAL_CPU_SHARES, msg);
        }
        stepLogBuilder.append("CPU: [" + deployConfig.getCpus() + "核 * " + instance + ", " + envResource.getCpus() + "核]");

        //mem
        long totalMem = deployConfig.getMemory() * instance;
        boolean memIsAdequacy = envResource.getMemory()
                .subtract(new BigDecimal(totalMem))
                .compareTo(BigDecimal.ZERO) >= 0;
        if (!memIsAdequacy) {
            String msg = String.format("发布配置[%s]当前发布操作内存资源不足, 请确认当前环境下是否有足够的内存", configName);
            throw new BizException(ILLEGAL_MEM, msg);
        }
        stepLogBuilder.append("内存: [" + deployConfig.getMemory() + "M * " + instance + ", " + envResource.getMemory() + "M]");

        //disk
        long totalDisk = deployConfig.getDisk() * instance;
        boolean diskIsAdequacy = envResource.getDisk()
                .subtract(new BigDecimal(totalDisk))
                .compareTo(BigDecimal.ZERO) >= 0;
        if (!diskIsAdequacy) {
            String msg = String.format("发布配置[%s]当前发布操作磁盘资源不足, 请确认当前环境下是否有足够的磁盘容量", configName);
            throw new BizException(ILLEGAL_DISK, msg);
        }
        stepLogBuilder.append("磁盘: [" + deployConfig.getDisk() + "G * " + instance + ", " + envResource.getDisk() + "G]");
    }

}
