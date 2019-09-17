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
package net.atayun.bazooka.base.annotation;

import java.lang.annotation.*;

/**
 * 验证用户的PMS节点权限
 * <p>
 * 需要验证数据权限的方法上添加 @PmsAuth 注解,appId 应存在 pathVariables 中
 *
 * @author WangSongJun
 * @date 2019-07-01
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PmsAuth {
    AuthType value() default AuthType.Read;

    enum AuthType {
        Read,
        Write
    }
}
