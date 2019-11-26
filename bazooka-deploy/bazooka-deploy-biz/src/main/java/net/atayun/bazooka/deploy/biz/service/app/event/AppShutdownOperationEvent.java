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

import mesosphere.client.common.ModelUtils;
import mesosphere.marathon.client.model.v2.App;
import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.enums.deploy.AppOperationEnum;
import net.atayun.bazooka.deploy.api.param.AppOperationEventParam;
import org.springframework.stereotype.Component;

/**
 * @author Ping
 * @date 2019-07-25
 * @see AppOperationEnum#STOP
 */
@Component
@StrategyNum(superClass = AbstractAppOperationEvent.class,
        number = "STOP",
        describe = "关闭服务"
)
public class AppShutdownOperationEvent extends AbstractAppSelfOperationEvent {

    @Override
    protected void customAppSetting(String detail, App app) {
        app.setInstances(0);
    }

    @Override
    public String getEventRemark(AppOperationEventParam appOperationEventParam) {
        String marathonConfig = getMarathonConfig(appOperationEventParam.getDetail());
        App app = ModelUtils.GSON.fromJson(marathonConfig, App.class);
        return "实例个数: " + app.getInstances() + " -> " + 0;
    }
}
