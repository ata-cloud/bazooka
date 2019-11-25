package net.atayun.bazooka.deploy.biz.v2.service.app.impl;

import mesosphere.marathon.client.model.v2.App;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.param.AppActionParam;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppOptService;
import org.springframework.stereotype.Service;

/**
 * @author Ping
 */
@Service
public class AppOptServiceImpl implements AppOptService {

    @Override
    public AppOpt saveOpt(AppActionParam appActionParam) {
        return null;
    }

    @Override
    public AppOpt selectById(Long optId) {
        return null;
    }

    @Override
    public AppOpt selectLastSuccessByAppIdAndEnv(Long appId, Long envId) {
        return null;
    }

    @Override
    public void updateById(AppOpt appopt) {

    }
}
