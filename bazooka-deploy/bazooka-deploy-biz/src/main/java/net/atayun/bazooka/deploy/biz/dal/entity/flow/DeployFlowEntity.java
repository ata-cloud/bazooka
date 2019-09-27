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
package net.atayun.bazooka.deploy.biz.dal.entity.flow;

import net.atayun.bazooka.deploy.biz.enums.flow.DeployFlowEnum;
import net.atayun.bazooka.deploy.biz.enums.status.BasicStatusEnum;
import com.youyu.common.entity.JdbcMysqlEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author Ping
 * @date 2019-07-11
 */
@Getter
@Setter
@Table(name = "deploy_deploy_flow")
public class DeployFlowEntity extends JdbcMysqlEntity<Long> {

    private Long deployId;

    private Integer flowNumber;

    @Column(name = "flow_type")
    private DeployFlowEnum flowType;

    @Column(name = "flow_status")
    private BasicStatusEnum flowStatus;

    public DeployFlowEntity callDeploy() {
        return this;
    }

    public DeployFlowEntity callSucc() {
        return this;
    }

    public DeployFlowEntity callFail() {
        return this;
    }
}
