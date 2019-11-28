package net.atayun.bazooka.deploy.biz.v2.service.app;

import com.youyu.common.api.PageData;
import net.atayun.bazooka.deploy.api.dto.AppRunningEventDto;
import net.atayun.bazooka.deploy.biz.v2.dto.app.AppOptWithStatusDto;
import net.atayun.bazooka.deploy.biz.v2.dto.app.AppOptHisPlatformDto;
import net.atayun.bazooka.deploy.biz.v2.dto.app.AppOptLogDto;
import net.atayun.bazooka.deploy.biz.v2.dto.app.AppActionDto;
import net.atayun.bazooka.deploy.biz.v2.dto.app.AppOptHisDto;
import net.atayun.bazooka.deploy.biz.v2.param.AppActionParam;
import net.atayun.bazooka.deploy.biz.v2.param.AppOptHisPlatformParam;
import net.atayun.bazooka.deploy.biz.v2.param.AppOptHisParam;

import java.util.List;

/**
 * @author Ping
 */
public interface AppActionService {

    /**
     * app 操作
     *
     * @param appActionParam 操作参数
     * @return action记录Id和类型
     */
    AppActionDto action(AppActionParam appActionParam);

    List<AppRunningEventDto> getAppRunningEvent(Long appId);

    PageData<AppOptHisDto> getAppOptHis(AppOptHisParam pageParam);

    PageData<AppOptHisPlatformDto> getAppOptHisPlatform(AppOptHisPlatformParam pageParam);

    String getAppOptHisPlatformDetail(Long optId);

    List<AppOptLogDto> getAppOptLog(Long optId);

    AppOptWithStatusDto getOptStatus(Long optId);
}
