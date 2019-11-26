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

import mesosphere.marathon.client.model.v2.App;
import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.enums.deploy.AppOperationEnum;
import net.atayun.bazooka.deploy.api.param.AppOperationEventParam;
import org.springframework.stereotype.Component;

/**
 * @author Ping
 * @date 2019-07-25
 * @see AppOperationEnum#START
 */
@Component
@StrategyNum(superClass = AbstractAppOperationEvent.class,
        number = "START",
        describe = "启动服务"
)
public class AppStartOperationEvent extends AbstractAppSelfOperationEvent {

    @Override
    protected void customAppSetting(String detail, App app) {
        app.setInstances(getAppResourceOperationEventPoJo(detail).getInstance());
    }

    @Override
    public String getEventRemark(AppOperationEventParam appOperationEventParam) {
        AppResourceOperationEventPoJo appResourceOperationEventPoJo =
                getAppResourceOperationEventPoJo(appOperationEventParam.getDetail());
        return "实例个数: " + 0 + " -> " + appResourceOperationEventPoJo.getInstance();
    }
}
