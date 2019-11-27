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
package net.atayun.bazooka.deploy.biz.v2.log;

import com.youyu.common.exception.BizException;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @author Ping
 * @date 2019-08-01
 */
public class LogConcat {

    private StringBuilder stringBuilder = new StringBuilder();

    public LogConcat() {
    }

    public LogConcat(String initString) {
        concat(initString);
    }

    public void concat(String content) {
        if (StringUtils.hasText(content)) {
            stringBuilder.append(content).append("\n");
        }
    }

    public void concat(Throwable throwable) {
        String content = "异常: ";
        if (throwable instanceof BizException) {
            BizException bizException = (BizException) throwable;
            content += "[Code: " + bizException.getCode() + "] " + bizException.getDesc();
        } else {
            content += Optional.ofNullable(throwable.getMessage())
                    .orElseGet(() -> Optional.ofNullable(throwable.getCause())
                            .map(Throwable::getMessage)
                            .orElse("异常信息未知"));

        }
        concat(content);
    }

    public String get() {
        return stringBuilder.toString();
    }
}
