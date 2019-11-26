package net.atayun.bazooka.deploy.biz.v2.service.app;

import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.enums.AppOptStatusEnum;
import net.atayun.bazooka.deploy.biz.v2.param.AppActionParam;

/**
 * @author Ping
 */
public interface AppOptService {

    /**
     * 保存app操作
     *
     * @param appActionParam 参数
     * @return 服务操作
     */
    AppOpt saveOpt(AppActionParam appActionParam);

    AppOpt selectById(Long optId);

    /**
     * 发布 回滚
     *
     * @param appId
     * @param envId
     * @return
     */
    AppOpt selectLastSuccessByAppIdAndEnv(Long appId, Long envId);

    void updateById(AppOpt appopt);

    AppOpt selectByAppDeployUuidAndVersion(String marathonDeploymentId, String marathonDeploymentVersion);

    void updateStatus(Long id, AppOptStatusEnum status);
}
