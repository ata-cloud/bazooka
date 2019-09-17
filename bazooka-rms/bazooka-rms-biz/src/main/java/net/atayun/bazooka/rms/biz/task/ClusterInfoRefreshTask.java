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
package net.atayun.bazooka.rms.biz.task;

import net.atayun.bazooka.rms.biz.service.RmsClusterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static net.atayun.bazooka.base.utils.StringUtil.eq;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 资源集群信息刷新任务:主要刷新集群状态信息、node节点信息和apps服务应用信息
 */
@Slf4j
@Component
public class ClusterInfoRefreshTask {

    @Autowired
    private Environment environment;

    @Autowired
    private RmsClusterService rmsClusterService;

    @Scheduled(cron = "0/30 * * * * ?")
    public void refreshClusterInfo() {
        log.info("集群信息刷新策略执行开始......");
        String property = environment.getProperty("spring.profiles.active");
        if (eq("dev", property)) {
            return;
        }
        rmsClusterService.refreshClusterInfo(rmsClusterService);
        log.info("集群信息刷新策略执行结束......");
    }
}
