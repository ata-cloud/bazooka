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
import lombok.extern.slf4j.Slf4j;
import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.bean.SpringContextBean;
import net.atayun.bazooka.base.enums.deploy.AppOperationEnum;
import net.atayun.bazooka.base.enums.deploy.AppOperationEventLogTypeEnum;
import net.atayun.bazooka.deploy.api.param.AppOperationEventParam;
import net.atayun.bazooka.deploy.biz.enums.status.AppOperationEventStatusEnum;
import net.atayun.bazooka.deploy.biz.log.AppOperationEventLog;
import net.atayun.bazooka.deploy.biz.log.LogConcat;
import net.atayun.bazooka.deploy.biz.service.status.EventStatusOpt;
import net.atayun.bazooka.rms.api.RmsDockerImageApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Ping
 * @date 2019-07-25
 * @see AppOperationEnum#DELETE_IMAGE
 */
@Slf4j
@Component
@StrategyNum(superClass = AbstractAppOperationEvent.class,
        number = "DELETE_IMAGE",
        describe = "删除服务相关docker镜像"
)
public class AppDeleteDockerImageOperationEvent extends AbstractAppOperationEvent implements EventStatusOpt {

    @Autowired
    private RmsDockerImageApi rmsDockerImageApi;

    @Override
    public String getEventRemark(AppOperationEventParam appOperationEventParam) {
        AppDeleteDockerImageOperationEventPoJo appDeleteDockerImageOperationEventPoJo =
                JSONObject.parseObject(appOperationEventParam.getDetail(), AppDeleteDockerImageOperationEventPoJo.class);
        return "镜像Tag: " + appDeleteDockerImageOperationEventPoJo.getDockerImageTag();
    }

    /**
     * 事件处理
     *
     * @param appOperationEventParam 参数
     * @param eventId                事件Id
     * @return subId
     */
    @Override
    public void doWork(AppOperationEventParam appOperationEventParam, Long eventId) {
        AppDeleteDockerImageOperationEventPoJo appDeleteDockerImageOperationEventPoJo =
                JSONObject.parseObject(appOperationEventParam.getDetail(), AppDeleteDockerImageOperationEventPoJo.class);

        AppOperationEventStatusEnum status = AppOperationEventStatusEnum.SUCCESS;
        LogConcat logConcat = new LogConcat("镜像删除结果:");
        try {
            rmsDockerImageApi.delete(appDeleteDockerImageOperationEventPoJo.getImageId());
        } catch (Throwable throwable) {
            logConcat.concat(throwable);
            status = AppOperationEventStatusEnum.FAILURE;
            log.warn(String.format("删除镜像异常 [event: %d]", eventId), throwable);
        } finally {
            updateEventStatus(eventId, status);
            logConcat.concat("状态: " + status);
            SpringContextBean.getBean(AppOperationEventLog.class).
                    saveAndMerge(eventId, AppOperationEventLogTypeEnum.DELETE_DOCKER_IMAGE, 2, logConcat.get());
        }
    }
}
