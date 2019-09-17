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
@Table(name = "rms_container")
public class RmsContainerEntity extends com.youyu.common.entity.JdbcMysqlEntity<Long> {
    /**
     * 容器编号
     */
    @Column(name = "container_id")
    private String containerId;

    /**
     * 容器状态(0:启动中，1:运行中，2:已关闭)
     */
    @Column(name = "state")
    private String state;

    /**
     * 容器实例数
     */
    @Column(name = "instances")
    private Integer instances;

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

    /**
     * 最新标识(0:否，1:是)
     */
    @Column(name = "latest_flag")
    private String latestFlag;
}