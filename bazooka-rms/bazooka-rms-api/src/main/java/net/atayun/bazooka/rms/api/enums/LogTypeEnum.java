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
package net.atayun.bazooka.pms.api.enums;

import com.youyu.common.api.IBaseResultCode;
import lombok.Getter;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 日志类型枚举
 */
@Getter
public enum LogTypeEnum implements IBaseResultCode {

    STDOUT("0", "/stdout", "标准日志"),
    STDERR("1", "/stderr", "标准错误日志");

    private String code;

    private String path;

    private String desc;

    LogTypeEnum(String code, String path, String desc) {
        this.code = code;
        this.path = path;
        this.desc = desc;
    }
}
