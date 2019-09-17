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
package net.atayun.bazooka.rms.biz.dal.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@Setter
@Table(name = "rms_env")
public class RmsEnvEntity extends com.youyu.common.entity.JdbcMysqlEntity<Long> {
    /**
     * 集群编号
     */
    @Column(name = "cluster_id")
    private Long clusterId;

    /**
     * 环境名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 环境代码
     */
    @Column(name = "code")
    private String code;

    /**
     * 环境状态(0:正常使用,1:暂停发布)
     */
    @Column(name = "state")
    private String state;

    /**
     * CPU
     */
    @Column(name = "cpus")
    private BigDecimal cpus;

    /**
     * 内存
     */
    @Column(name = "memory")
    private BigDecimal memory;

    /**
     * 磁盘
     */
    @Column(name = "disk")
    private BigDecimal disk;
}