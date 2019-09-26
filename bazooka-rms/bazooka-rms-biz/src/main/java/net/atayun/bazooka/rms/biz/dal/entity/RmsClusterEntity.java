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

import com.youyu.common.entity.JdbcMysqlEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 资源集群 实体
 */
@Getter
@Setter
@Table(name = "rms_cluster")
public class RmsClusterEntity extends JdbcMysqlEntity<Long> {
    /**
     * 集群名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 机房类型
     */
    @Column(name = "room_type")
    private String roomType;

    /**
     * 集群状态: 0:正常(绿色) 1:可用(黄色) 2:异常(红色)
     */
    @Column(name = "status")
    private String status;

    /**
     * 集群类型 0:DC/OS
     */
    @Column(name = "type")
    private String type;

    /**
     * 集群版本信息
     */
    @Column(name = "version")
    private String version;

    /**
     * cpu信息
     */
    @Column(name = "cpu")
    private BigDecimal cpu;

    /**
     * 内存信息
     */
    @Column(name = "memory")
    private BigDecimal memory;

    /**
     * 磁盘信息
     */
    @Column(name = "disk")
    private BigDecimal disk;

    /**
     * 是否开启监听
     */
    @Column(name = "enable_monitor")
    private Boolean enableMonitor;

    public Long getClusterId() {
        return getId();
    }

}