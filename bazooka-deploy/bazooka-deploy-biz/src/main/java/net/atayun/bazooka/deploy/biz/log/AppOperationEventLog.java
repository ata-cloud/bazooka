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
package net.atayun.bazooka.deploy.biz.log;

import net.atayun.bazooka.base.enums.deploy.AppOperationEventLogTypeEnum;

/**
 * @author Ping
 * @date 2019-08-01
 */
public interface AppOperationEventLog {

    default void save(Long eventId, AppOperationEventLogTypeEnum appOperationEventLogTypeEnum, String content) {
        save(eventId, appOperationEventLogTypeEnum, null, content);
    }

    void save(Long eventId, AppOperationEventLogTypeEnum appOperationEventLogTypeEnum, Integer part, String content);

    String get(Long eventId, AppOperationEventLogTypeEnum appOperationEventLogTypeEnum);

    void mergePartFile(Long eventId, AppOperationEventLogTypeEnum appOperationEventLogTypeEnum);

    default void saveAndMerge(Long eventId, AppOperationEventLogTypeEnum appOperationEventLogTypeEnum, String content) {
        save(eventId, appOperationEventLogTypeEnum, content);
        mergePartFile(eventId, appOperationEventLogTypeEnum);
    }

    default void saveAndMerge(Long eventId, AppOperationEventLogTypeEnum appOperationEventLogTypeEnum, Integer part, String content) {
        save(eventId, appOperationEventLogTypeEnum, part, content);
        mergePartFile(eventId, appOperationEventLogTypeEnum);
    }
}
