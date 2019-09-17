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
package net.atayun.bazooka.base.service.impl;


import net.atayun.bazooka.base.service.BatchService;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Transactional;


import java.lang.reflect.Method;
import java.util.List;


import static net.atayun.bazooka.base.utils.StringUtil.eq;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.ibatis.session.ExecutorType.BATCH;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年5月30日 10:00:00
 * @work Mybatis批处理 Service impl
 */
public class BatchServiceImpl implements BatchService, ApplicationContextAware {
    /**
     * 默认200条一次批处理
     */
    private final static int BATCH_SIZE = 200;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public <E, Mapper> void batchDispose(List<E> list, Class<Mapper> mapperClass, String methodName) {
        doBatchDispose(list, mapperClass, methodName, BATCH_SIZE);
    }

    @Override
    public <E, Mapper> void batchDispose(List<E> list, Class<Mapper> mapperClass, String methodName, int batchSize) {
        doBatchDispose(list, mapperClass, methodName, batchSize);
    }

    @Override
    @Transactional(propagation = REQUIRES_NEW)
    public <E, Mapper> void independentBatchDispose(List<E> list, Class<Mapper> mapperClass, String methodName) {
        doBatchDispose(list, mapperClass, methodName, BATCH_SIZE);
    }

    @Override
    @Transactional(propagation = REQUIRES_NEW)
    public <E, Mapper> void independentBatchDispose(List<E> list, Class<Mapper> mapperClass, String methodName, int batchSize) {
        doBatchDispose(list, mapperClass, methodName, batchSize);
    }

    /**
     * 批量处理
     *
     * @param list
     * @param mapperClass
     * @param methodName
     * @param batchSize
     * @param <E>
     * @param <Mapper>
     * @return
     */
    private <E, Mapper> void doBatchDispose(List<E> list, Class<Mapper> mapperClass, String methodName, int batchSize) {
        check(mapperClass, methodName);

        list = removeNull4List(list);
        if (isEmpty(list)) {
            return;
        }

        doBatchSql(list, mapperClass, methodName, batchSize);
    }

    /**
     * 批量处理Sql(批量操作支持增删改)
     *
     * @param list
     * @param mapperClass
     * @param methodName
     * @param batchSize
     * @param <E>
     * @param <Mapper>
     */
    private <E, Mapper> void doBatchSql(List<E> list, Class<Mapper> mapperClass, String methodName, int batchSize) {
        int size = list.size();
        DefaultSqlSessionFactory sqlSessionFactory = (DefaultSqlSessionFactory) applicationContext.getBean(SqlSessionFactory.class);

        try (SqlSession batchSqlSession = sqlSessionFactory.openSession(BATCH, false)) {
            Mapper mapper = batchSqlSession.getMapper(mapperClass);
            Method declaredMethod = getMethod(methodName, mapper);

            for (int i = 0; i < size; i++) {
                declaredMethod.invoke(mapper, list.get(i));
                if (i != 0 && (i % batchSize) == 0) {
                    batchSqlSession.commit();
                }
            }
            batchSqlSession.commit();
        } catch (Exception ex) {
            throw new RuntimeException("mybatis批量提交异常信息:" + ex.getMessage());
        }
    }

    /**
     * 移除为null的数据
     *
     * @param list
     * @param <E>
     * @return
     */
    private <E> List<E> removeNull4List(List<E> list) {
        if (isEmpty(list)) {
            return null;
        }

        List<E> cleanList = list.stream().filter(e -> nonNull(e)).collect(toList());
        return cleanList;
    }

    /**
     * 获取方法
     *
     * @param methodName
     * @param mapper
     * @param <Mapper>
     * @return
     */
    private <Mapper> Method getMethod(String methodName, Mapper mapper) {
        Method[] declaredMethods = mapper.getClass().getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (eq(methodName, method.getName())) {
                return method;
            }
        }
        throw new RuntimeException("没有找到方法名称:" + methodName + "对应的方法!");
    }

    /**
     * 检查mapper和方法
     *
     * @param mapperClass
     * @param methodName
     * @param <Mapper>
     */
    private <Mapper> void check(Class<Mapper> mapperClass, String methodName) {
        if (isNull(mapperClass)) {
            throw new RuntimeException("mapper信息不能为空!");
        }

        if (isBlank(methodName)) {
            throw new RuntimeException("mapper方法名称不能为空!");
        }
    }

}
