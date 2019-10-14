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
package net.atayun.bazooka.deploy.biz.service.deploy.strategy.setup;

import net.atayun.bazooka.deploy.biz.log.AppOperationEventLog;
import net.atayun.bazooka.deploy.biz.service.deploy.strategy.WorkDetailPojo;
import lombok.Getter;
import net.atayun.bazooka.combase.enums.deploy.DeployModeEnum;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Ping
 * @date 2019-07-22
 */
public abstract class AbstractSetUpFlowStrategy implements ApplicationContextAware {

    @Getter
    private ApplicationContext applicationContext;

    @Getter
    @Autowired
    private AppOperationEventLog appOperationEventLog;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 发布流中 sep_up中的部分资源检查
     * {@link DeployModeEnum#BUILD} 需要校验分支是否存在且有效
     * {@link DeployModeEnum#DOCKER_IMAGE} 需要校验镜像是否存在且有效
     *
     * @param workDetailPojo 实体数据
     */
    public abstract void setUpCheck(WorkDetailPojo workDetailPojo);

    /**
     * 准备工作完成后的操作
     *
     * @param workDetailPojo 实体数据
     */
    public abstract void afterAll(WorkDetailPojo workDetailPojo);
}
