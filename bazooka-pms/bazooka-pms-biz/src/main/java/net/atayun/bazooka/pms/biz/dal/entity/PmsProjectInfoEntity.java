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

import com.youyu.common.entity.JdbcMysqlEntity;
import com.youyu.common.enums.IsDeleted;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author rache
 * @date 2019-07-12
 */
@Data
@Table(name = "pms_project_info")
public class PmsProjectInfoEntity extends JdbcMysqlEntity<Long> {
    private String projectName;
    private String projectCode;
    private String description;
    private Integer gitlabGroupId;
    @Column(name="is_deleted")
    private IsDeleted isDeleted;
}
