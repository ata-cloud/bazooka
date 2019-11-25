package net.atayun.bazooka.deploy.biz.v2.service.app;

import net.atayun.bazooka.deploy.biz.v2.dto.app.AppActionDto;
import net.atayun.bazooka.deploy.biz.v2.param.AppActionParam;

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
}
