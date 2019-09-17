/*
 *    Copyright 2018-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.atayun.bazooka.deploy.biz.service.app.event;

import com.alibaba.fastjson.JSONObject;
import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.deploy.api.param.AppOperationEventParam;
import net.atayun.bazooka.deploy.biz.dal.entity.app.AppOperationEventMarathonEntity;
import net.atayun.bazooka.deploy.biz.service.app.AppOperationEventMarathonService;
import com.youyu.common.exception.BizException;
import mesosphere.client.common.ModelUtils;
import mesosphere.marathon.client.model.v2.App;
import net.atayun.bazooka.base.enums.deploy.AppOperationEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static net.atayun.bazooka.deploy.biz.constants.DeployResultCodeConstants.NO_SUCCESS_EVENT_WITH_MARATHON_ERR_CODE;

/**
 * @author Ping
 * @date 2019-07-25
 * @see AppOperationEnum#ROLLBACK
 */
@Component
@StrategyNum(superClass = AbstractAppOperationEvent.class,
        number = "ROLLBACK",
        describe = "回滚操作"
)
public class AppRollbackOperationEvent extends AbstractAppOperationEventWithCallMarathon {

    @Autowired
    private AppOperationEventMarathonService appOperationEventMarathonService;


    @Override
    public String getMarathonConfig(String detail) {
        AppRollbackOperationEventPoJo appRollbackOperationEventPoJo = JSONObject.parseObject(detail, AppRollbackOperationEventPoJo.class);
        AppOperationEventMarathonEntity appOperationEventMarathonEntity =
                appOperationEventMarathonService.selectByEventId(appRollbackOperationEventPoJo.getTemplateEventId());
        if (appOperationEventMarathonEntity == null) {
            throw new BizException(NO_SUCCESS_EVENT_WITH_MARATHON_ERR_CODE, "获取Marathon相关事件错误");
        }
        return appOperationEventMarathonEntity.getMarathonConfig();
    }

    /**
     * 自定义配置服务Marathon配置
     *
     * @param detail 详情
     * @param app    Marathon app
     */
    @Override
    protected void customAppSetting(String detail, App app) {

    }

    @Override
    public String getEventRemark(AppOperationEventParam appOperationEventParam) {
        String marathonConfig = getMarathonConfig(appOperationEventParam.getDetail());
        String image = ModelUtils.GSON.fromJson(marathonConfig, App.class).getContainer().getDocker().getImage();
        String[] split = image.split(":");
        String tag = split[split.length - 1];
        return "镜像tag: " + tag;
    }
}
