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
 * @date 2019年8月10日 17:00:00
 * @work 集群容器task状态枚举
 */
@Getter
public enum ClusterTaskHealthEnum implements IBaseResultCode {

    HEALTH("0", "健康"),
    UN_HEALTH("1", "不健康"),
    NO_HEALTH_CHECK("2", "没有健康检查"),
    NOT_EXIST_HEALTH("3", "不存在健康");

    private String code;

    private String desc;

    ClusterTaskHealthEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
