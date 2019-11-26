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
package net.atayun.bazooka.deploy.biz.dal.entity.app;

import lombok.Getter;
import lombok.Setter;
import net.atayun.bazooka.base.enums.deploy.AppOperationEnum;
import net.atayun.bazooka.deploy.biz.enums.status.AppOperationEventStatusEnum;

import javax.persistence.Column;
import java.time.LocalDateTime;

/**
 * @author Ping
 * @date 2019-08-05
 */
@Getter
@Setter
public class AppOperateEventHistoryEntity {

    private Long eventId;

    private LocalDateTime operateDatetime;

    private String operator;

    private AppOperationEnum event;

    private String remark;

    @Column(name = "status")
    private AppOperationEventStatusEnum status;

    private String detail;
}
