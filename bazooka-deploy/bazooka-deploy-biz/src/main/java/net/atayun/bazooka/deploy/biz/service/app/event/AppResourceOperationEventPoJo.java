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
package net.atayun.bazooka.deploy.biz.service.app.event;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Ping
 * @date 2019-07-26
 */
@Getter
@Setter
public class AppResourceOperationEventPoJo {

    private Long appId;

    private Long envId;

    private Double cpu;

    private Double memory;

    private Double disk;

    private Integer instance;
}
