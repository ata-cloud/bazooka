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
package net.atayun.bazooka.deploy.biz.enums;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

/**
 * @author Ping
 * @date 2019-07-26
 */
public enum TimeGranularityEnum {

    /**
     * 周
     */
    WEEK {
        @Override
        public LocalDateTime getLeftDatetime() {
            return LocalDate.now().with(ChronoField.DAY_OF_WEEK, 1).atStartOfDay();
        }
    },

    /**
     * 月
     */
    MONTH {
        @Override
        public LocalDateTime getLeftDatetime() {
            return LocalDate.now().withDayOfMonth(1).atStartOfDay();
        }
    },

    /**
     * 年
     */
    YEAR {
        @Override
        public LocalDateTime getLeftDatetime() {
            return LocalDate.now().withDayOfYear(1).atStartOfDay();
        }
    };

    public abstract LocalDateTime getLeftDatetime();
}
