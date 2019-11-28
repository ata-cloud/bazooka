package net.atayun.bazooka.deploy.biz.v2.config;

import net.atayun.bazooka.deploy.biz.v2.service.app.threadpool.AppActionThreadPool;
import net.atayun.bazooka.deploy.biz.v2.service.app.threadpool.FlowStepThreadPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ping
 */
@Configuration
public class DeployThreadPoolConfiguration {

    @Bean
    public AppActionThreadPool appActionThreadPool() {
        return new AppActionThreadPool();
    }

    @Bean
    public FlowStepThreadPool flowStepThreadPool() {
        return new FlowStepThreadPool();
    }
}
