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
package net.atayun.bazooka.base.mlb;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author: hanxiaorui
 * @Date: 2019/7/30 09:39
 * @Description: Mlb信息
 */
@Getter
@Setter
public class MlbServiceDto {

    /**
     * 服务名
     */
    private String service;

    /**
     * 端口
     */
    private int port;
}
