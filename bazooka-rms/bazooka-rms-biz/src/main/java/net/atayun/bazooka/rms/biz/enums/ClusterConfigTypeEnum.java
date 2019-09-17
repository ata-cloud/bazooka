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
package net.atayun.bazooka.rms.biz.enums;

import com.youyu.common.api.IBaseResultCode;
import lombok.Getter;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月18日 17:00:00
 * @work 集群配置类型枚举
 */
@Getter
public enum ClusterConfigTypeEnum implements IBaseResultCode {

    MARATHON("0", "集群master") {
        @Override
        public boolean canRefreshDcosStatus() {
            return true;
        }
    },
    MLB("1", "集群mlb详情") {
        @Override
        public boolean canRefreshDcosStatus() {
            return true;
        }
    },
    DOCKER_HUB("2", "镜像库") {
        @Override
        public boolean canRefreshDcosStatus() {
            return true;
        }
    },
    JENKINS("3", "jenkins"),
    GITLAB("4", "gitLab"),
    ATACLOUD("5", "ataCloud");

    private String code;

    private String desc;

    ClusterConfigTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public boolean canRefreshDcosStatus() {
        return false;
    }
}
