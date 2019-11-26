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
import com.youyu.common.exception.BizException;
import lombok.Getter;
import net.atayun.bazooka.deploy.biz.dal.entity.app.EventWithMarathonEntity;
import net.atayun.bazooka.deploy.biz.service.app.AppOperationEventService;
import org.springframework.beans.factory.annotation.Autowired;

import static net.atayun.bazooka.deploy.biz.constants.DeployResultCodeConstants.NO_SUCCESS_EVENT_WITH_MARATHON_ERR_CODE;

/**
 * @author Ping
 * @date 2019-07-26
 */
public abstract class AbstractAppSelfOperationEvent extends AbstractAppOperationEventWithCallMarathon {

    @Getter
    @Autowired
    private AppOperationEventService appOperationEventService;

    @Override
    public String getMarathonConfig(String detail) {
        AppResourceOperationEventPoJo appResourceOperationEventPoJo = getAppResourceOperationEventPoJo(detail);
        Long appId = appResourceOperationEventPoJo.getAppId();
        Long envId = appResourceOperationEventPoJo.getEnvId();
        EventWithMarathonEntity eventWithMarathonEntity =
                appOperationEventService.selectTheLastSuccessEventWithMarathon(appId, envId);
        if (eventWithMarathonEntity == null) {
            throw new BizException(NO_SUCCESS_EVENT_WITH_MARATHON_ERR_CODE, "获取Marathon相关事件错误");
        }
        return eventWithMarathonEntity.getMarathonConfig();
    }

    public AppResourceOperationEventPoJo getAppResourceOperationEventPoJo(String detail) {
        return JSONObject.parseObject(detail, AppResourceOperationEventPoJo.class);
    }
}
