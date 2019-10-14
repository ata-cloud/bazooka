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
package net.atayun.bazooka.gateway.application;

import net.atayun.bazooka.gateway.component.strategy.ClusterDeployStrategy;
import net.atayun.bazooka.pms.api.api.RmsClusterApi;
import net.atayun.bazooka.pms.api.dto.rsp.ClusterMarathonConfigRspDto;
import com.youyu.common.api.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

import static net.atayun.bazooka.combase.bean.StrategyNumBean.getBeanInstance;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月29日 17:00:00
 * @work 重写SpringApplication类, 扩展afterRefresh方法, 增加发布事件监听
 */
@Slf4j
public class OpsSpringApplication extends SpringApplication {

    public OpsSpringApplication(Class<?>... primarySources) {
        super(primarySources);
    }

    @Override
    protected void afterRefresh(ConfigurableApplicationContext context, ApplicationArguments args) {
        try {
            RmsClusterApi rmsClusterApi = context.getBean(RmsClusterApi.class);
            Result<List<ClusterMarathonConfigRspDto>> result = rmsClusterApi.getClusterMarathonConfigs();
            List<ClusterMarathonConfigRspDto> clusterMarathonConfigs = result.ifNotSuccessThrowException().getData();
            if (isEmpty(clusterMarathonConfigs)) {
                return;
            }

            for (ClusterMarathonConfigRspDto clusterMarathonConfig : clusterMarathonConfigs) {
                Boolean enableMonitor = clusterMarathonConfig.getEnableMonitor();
                if (enableMonitor) {
                    doMonitorDeployEvent(clusterMarathonConfig);
                }
            }
        } catch (Exception ex) {
            log.error("自定义启动后刷新异常信息:[{}]", getFullStackTrace(ex));
        }
    }

    /**
     * 执行监听发布事件
     *
     * @param clusterMarathonConfig
     */
    private void doMonitorDeployEvent(ClusterMarathonConfigRspDto clusterMarathonConfig) {
        ClusterDeployStrategy clusterDeployStrategy = getBeanInstance(ClusterDeployStrategy.class, clusterMarathonConfig.getClusterType());
        clusterDeployStrategy.monitorDeployEvent(clusterMarathonConfig);
    }
}
