package net.atayun.bazooka.deploy.biz.v2.service.app;

import net.atayun.bazooka.base.enums.AppOptEnum;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptWithPlatform;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptCounts;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptHis;
import net.atayun.bazooka.deploy.biz.v2.enums.AppOptStatusEnum;
import net.atayun.bazooka.deploy.biz.v2.param.AppActionParam;
import net.atayun.bazooka.deploy.biz.v2.param.AppOptHisPlatformParam;
import net.atayun.bazooka.deploy.biz.v2.param.AppOptHisParam;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Ping
 */
public interface AppOptService {

    AppOpt saveOpt(AppActionParam appActionParam);

    AppOpt selectById(Long optId);

    AppOpt selectLastSuccessByAppIdAndEnv(Long appId, Long envId);

    void updateById(AppOpt appOpt);

    AppOpt selectByAppDeployUuidAndVersionForMarathon(String uuid, String version);

    void update(AppOpt appOpt);

    List<AppOpt> selectByAppIdAndStatus(Long appId, AppOptStatusEnum status);

    List<AppOptHis> getAppOptHis(AppOptHisParam pageParam);

    List<AppOptWithPlatform> getAppOptHisPlatform(AppOptHisPlatformParam pageParam);

    AppOpt selectByStatusAndOpt(Long appId, Long envId, AppOptStatusEnum statusEnum, AppOptEnum appOptEnum);

    AppOpt isProcessing(Long appId, Long envId);

    List<AppOptCounts> appOptCountsByProject(Long projectId, LocalDateTime leftDatetime);
}
