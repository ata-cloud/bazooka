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
package net.atayun.bazooka.pms.biz.dal.entity;

import net.atayun.bazooka.base.enums.deploy.AppOperationEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pms_app_deploy_status")
public class  PmsAppDeployStatusEntity extends com.youyu.common.entity.JdbcMysqlEntity<Long> {
    public PmsAppDeployStatusEntity(Long appId, Long envId) {
        this.appId = appId;
        this.envId = envId;
    }


    /**
     * 应用ID
     */
    @Column(name = "app_id")
    private Long appId;

    /**
     * 环境ID
     */
    @Column(name = "env_id")
    private Long envId;

    /**
     * 部署中
     */
    @Column(name = "deploying")
    private Boolean deploying;

    /**
     * 部署类型
     */
    @Column(name = "deploy_type")
    private AppOperationEnum deployType;
}