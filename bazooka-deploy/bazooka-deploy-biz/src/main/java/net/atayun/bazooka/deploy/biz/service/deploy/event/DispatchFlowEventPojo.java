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
package net.atayun.bazooka.deploy.biz.service.deploy.event;

import net.atayun.bazooka.deploy.biz.enums.status.BasicStatusEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ping
 * @date 2019-07-11
 */
@Getter
@Setter
public class DispatchFlowEventPojo {

    private Long deployId;

    private Integer currentFlowNumber;

    private BasicStatusEnum currentFlowStatus;

    public DispatchFlowEventPojo(Long deployId, Integer currentFlowNumber, BasicStatusEnum currentFlowStatus) {
        this.deployId = deployId;
        this.currentFlowNumber = currentFlowNumber;
        this.currentFlowStatus = currentFlowStatus;
    }

    public DispatchFlowEventPojo(Long deployId, Integer currentFlowNumber) {
        this.deployId = deployId;
        this.currentFlowNumber = currentFlowNumber;
    }
}
