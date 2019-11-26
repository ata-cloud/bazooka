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
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Ping
 * @date 2019-07-25
 * @see AppOperationEnum#SCALE
 */
@Component
@StrategyNum(superClass = AbstractAppOperationEvent.class,
        number = "SCALE",
        describe = "扩/缩容服务"
)
public class AppScaleOperationEvent extends AbstractAppSelfOperationEvent {

    @Override
    protected void customAppSetting(String detail, App app) {
        AppResourceOperationEventPoJo appResourceOperationEventPoJo = getAppResourceOperationEventPoJo(detail);
        app.setCpus(appResourceOperationEventPoJo.getCpu());
        app.setMem(appResourceOperationEventPoJo.getMemory());
        app.setDisk(appResourceOperationEventPoJo.getDisk());
        app.setInstances(appResourceOperationEventPoJo.getInstance());
    }

    @Override
    public String getEventRemark(AppOperationEventParam appOperationEventParam) {
        @NotNull String detail = appOperationEventParam.getDetail();
        AppResourceOperationEventPoJo appResourceOperationEventPoJo = getAppResourceOperationEventPoJo(detail);
        String marathonConfig = getMarathonConfig(detail);
        App app = ModelUtils.GSON.fromJson(marathonConfig, App.class);


        //instance
        Integer instance = Optional.ofNullable(appResourceOperationEventPoJo.getInstance()).orElse(0);
        String instanceChange = "";
        if (instance.compareTo(app.getInstances()) != 0) {
            instanceChange = "实例个数: " + app.getInstances() + " -> " + instance;
        }

        //cpu
        Double cpu = Optional.ofNullable(appResourceOperationEventPoJo.getCpu()).orElse(0D);
        String cpuChange = "";
        if (cpu.compareTo(app.getCpus()) != 0) {
            cpuChange = "CPU Shares: " + app.getCpus() + " -> " + cpu;
        }

        //mem
        Double mem = Optional.ofNullable(appResourceOperationEventPoJo.getMemory()).orElse(0D);
        String memChange = "";
        if (mem.compareTo(app.getMem()) != 0) {
            memChange = "内存大小: " + app.getMem() + " -> " + mem;
        }

        return Stream.of(instanceChange, cpuChange, memChange)
                .filter(StringUtils::hasText)
                .collect(Collectors.joining(", "));
    }
}
