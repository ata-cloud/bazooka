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
package net.atayun.bazooka.combase.annotation;

import java.lang.annotation.*;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年5月30日 10:00:00
 * @work 策略注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface StrategyNum {

    /**
     * 父类Class类型
     *
     * @return
     */
    Class<?> superClass();

    /**
     * 编号
     *
     * @return
     */
    String number() default "0";

    /**
     * 描述
     *
     * @return
     */
    String describe() default "描述";
}
