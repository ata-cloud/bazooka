package net.atayun.bazooka.base.bean;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;

/**
 * @author Ping
 */
public class SpringApplicationEventPublisher implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static void publish(ApplicationEvent applicationEvent) {
        applicationContext.publishEvent(applicationEvent);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringApplicationEventPublisher.applicationContext = applicationContext;
    }
}
