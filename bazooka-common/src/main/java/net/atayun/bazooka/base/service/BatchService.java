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
package net.atayun.bazooka.base.service;

import java.util.List;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年5月30日 10:00:00
 * @work Mybatis批处理 Service
 */
public interface BatchService {

    /**
     * mapper批量处理(依赖外部事务)
     *
     * @param list        数据
     * @param mapperClass mapper
     * @param methodName  方法名
     * @param <E>
     * @param <Mapper>
     * @return
     */
    <E, Mapper> void batchDispose(List<E> list, Class<Mapper> mapperClass, String methodName);

    /**
     * mapper批量处理(依赖外部事务)
     *
     * @param list        数据
     * @param mapperClass mapper
     * @param methodName  方法名
     * @param batchSize   批量大小
     * @param <E>
     * @param <Mapper>
     * @return
     */
    <E, Mapper> void batchDispose(List<E> list, Class<Mapper> mapperClass, String methodName, int batchSize);

    /**
     * mapper批量处理(独立事务)
     *
     * @param list        数据
     * @param mapperClass mapper
     * @param methodName  方法名
     * @param <E>
     * @param <Mapper>
     * @return
     */
    <E, Mapper> void independentBatchDispose(List<E> list, Class<Mapper> mapperClass, String methodName);

    /**
     * mapper批量处理(独立事务)
     *
     * @param list        数据
     * @param mapperClass mapper
     * @param methodName  方法名
     * @param batchSize   批量大小
     * @param <E>
     * @param <Mapper>
     * @return
     */
    <E, Mapper> void independentBatchDispose(List<E> list, Class<Mapper> mapperClass, String methodName, int batchSize);
}
