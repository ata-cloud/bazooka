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
package net.atayun.bazooka.combase.bean;


import net.atayun.bazooka.combase.annotation.StrategyNum;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import static net.atayun.bazooka.combase.utils.StringUtil.checkNonBlank;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年5月30日 10:00:00
 * @work 策略注解StrategyNum解析
 */
public class StrategyNumBean implements ApplicationContextAware, InitializingBean {
    /**
     * 策略beanMap
     */
    private static final Map<String, Object> STRATEGY_BEAN_MAP = new HashMap<>();
    /**
     * 容器上下文
     */
    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, Object> strategyNumMap = applicationContext.getBeansWithAnnotation(StrategyNum.class);
        fillStrategyBeanMap(strategyNumMap);
    }

    /**
     * 根据bean class小写名称获取bean
     *
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T getBeanByClass(Class<T> tClass) {
        requireNonNull(tClass, "请传入正确的Class的类:[" + tClass + "]!");

        return (T) getBeanByName(lowerCase(tClass.getSimpleName()));
    }

    /**
     * 获取Spring Bean 实例
     *
     * @param tClass
     * @param parameters
     * @param <T>
     * @return
     */
    public static <T> T getBeanInstance(Class<T> tClass, String... parameters) {
        requireNonNull(tClass, "请传入正确的Class的类:[" + tClass + "]!");

        return (T) getBeanByName(join(tClass.getSimpleName(), join(parameters)));
    }


    public static <T> List<T> getBeanInstances(Class<T> tClass, String parameters) {
        return null;
    }

    /**
     * 根据bean name获取bean
     *
     * @param beanName
     * @return
     */
    public static Object getBeanByName(String beanName) {
        checkNonBlank(beanName, "请传入正确的Bean的名称:[" + beanName + "]!");

        return requireNonNull(STRATEGY_BEAN_MAP.get(beanName), "根据beanName:[" + beanName + "]未查询到该Bean!");
    }

    /**
     * 填充策略beanMap
     *
     * @param strategyNumMap
     */
    private void fillStrategyBeanMap(Map<String, Object> strategyNumMap) {
        if (isEmpty(strategyNumMap)) {
            return;
        }

        for (Entry<String, Object> entry : strategyNumMap.entrySet()) {
            doFillStrategyBeanMap(entry);
        }
    }

    /**
     * 执行填充策略beanMap
     *
     * @param entry
     */
    private void doFillStrategyBeanMap(Entry<String, Object> entry) {
        Object strategyNumBean = entry.getValue();
        Class<?> strategyNumClass = strategyNumBean.getClass();
        StrategyNum annotation = findAnnotation(strategyNumClass, StrategyNum.class);

        String combineKey = join(annotation.superClass().getSimpleName(), annotation.number());
        String classNameKey = lowerCase(strategyNumClass.getSimpleName());
        requireBeanNotExist(combineKey, classNameKey);

        STRATEGY_BEAN_MAP.put(combineKey, strategyNumBean);
        STRATEGY_BEAN_MAP.put(classNameKey, strategyNumBean);
    }

    /**
     * parameters对应的STRATEGY_BEAN_MAP若存在则抛异常
     *
     * @param parameters
     */
    private static void requireBeanNotExist(String... parameters) {
        for (String parameter : parameters) {
            Object bean = STRATEGY_BEAN_MAP.get(parameter);
            if (nonNull(bean)) {
                throw new RuntimeException("存在key=[" + parameter + "]对应的单例对象:[" + bean + "]!");
            }
        }
    }
}
