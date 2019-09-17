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
package net.atayun.bazooka.pms.api.param;

import lombok.*;

import javax.validation.constraints.Size;

/**
 * 健康检查
 *
 * @author WangSongJun
 * @date 2019-07-16
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HealthCheck {
    /**
     * 初始化等待
     */
    private int gracePeriodSeconds;

    /**
     * 间隔
     */
    private int intervalSeconds;

    /**
     * 最大失败次数
     */
    private int maxConsecutiveFailures;

    private int portIndex;

    /**
     * 超时时间
     */
    private int timeoutSeconds;

    /**
     * 延时
     */
    private int delaySeconds = 15;

    /**
     * 协议
     */
    private Protocol protocol;

    private IpProtocol ipProtocol;

    @Size(max = 128,message = "路径长度不能超过128")
    private String path;

    public enum Protocol {
        TCP,
        HTTP
    }
    public enum IpProtocol {
        IPv4,
        IPv6
    }
}
