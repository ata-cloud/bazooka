package net.atayun.bazooka.deploy.biz.v2.service.app.impl;

import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dto.app.AppActionDto;
import net.atayun.bazooka.deploy.biz.v2.param.AppActionParam;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppActionService;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppOptService;
import net.atayun.bazooka.deploy.biz.v2.service.app.opt.AppOptWorker;
import net.atayun.bazooka.deploy.biz.v2.service.app.threadpool.AppActionThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Ping
 */
@Service
public class AppActionServiceImpl implements AppActionService {

    @Autowired
    private AppOptService appOptService;

    @Override
    public AppActionDto action(AppActionParam appActionParam) {
        AppOpt appOpt = appOptService.saveOpt(appActionParam);
        AppActionThreadPool.execute(() -> new AppOptWorker(appOpt).doWork());
        return new AppActionDto(appOpt.getId(), appOpt.getOpt());
    }
}
