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
 * @work 资源集群配置 实体
 */
@Getter
@Setter
@Table(name = "rms_cluster_config")
public class RmsClusterConfigEntity extends JdbcMysqlEntity<Long> {
    /**
     * 集群外键
     */
    @Column(name = "cluster_id")
    private Long clusterId;

    /**
     * 集群类型: 0:集群master 1:集群mlb 2:镜像库 3:Jenkins 4:GitLab 5:AtaCloud
     */
    @Column(name = "type")
    private String type;

    /**
     * url地址
     */
    @Column(name = "url")
    private String url;

    /**
     * 状态: 0:正常(绿色) 1:可用(黄色) 2:异常(红色)
     */
    @Column(name = "status")
    private String status;

    /**
     * 版本
     */
    @Column(name = "version")
    private String version;

    /**
     * 获取类型名称
     *
     * @param type
     * @return
     */
    public static String getTypeName(String type) {
        switch (type) {
            case "0":
                return "DC/OS";
            case "1":
                return "MLB";
            case "2":
                return "镜像库";
            case "3":
                return "Jenkins";
            case "4":
                return "GitLab";
            case "5":
                return "AtaCloud";
            default:
                return type;
        }
    }
}