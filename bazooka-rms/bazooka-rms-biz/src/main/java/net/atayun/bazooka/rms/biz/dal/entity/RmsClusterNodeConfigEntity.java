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

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 资源集群节点配置 实体
 */
@Getter
@Setter
@Table(name = "rms_cluster_node_config")
public class RmsClusterNodeConfigEntity extends JdbcMysqlEntity<Long> {
    /**
     * 集群节点外键
     */
    @Column(name = "cluster_node_id")
    private Long clusterNodeId;

    /**
     * cmd命令
     */
    @Column(name = "cmd")
    private String cmd;
}