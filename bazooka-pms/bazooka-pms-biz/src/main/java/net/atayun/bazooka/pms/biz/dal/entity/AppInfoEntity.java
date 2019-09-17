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

import net.atayun.bazooka.pms.api.enums.AppKindEnum;
import com.youyu.common.entity.JdbcMysqlEntity;
import com.youyu.common.enums.IsDeleted;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 应用详情
 *
 * @author WangSongJun
 * @date 2019-07-16
 */
@Getter
@Setter
@NoArgsConstructor
@Table(name = "pms_app_info")
public class AppInfoEntity extends JdbcMysqlEntity<Long> {
    public AppInfoEntity(Long projectId, String appName, String appCode, AppKindEnum appKind, String description, Long leaderId, String gitUrl, Long gitCredentialId) {
        this.projectId = projectId;
        this.appName = appName;
        this.appCode = appCode;
        this.appKind = appKind;
        this.description = description;
        this.leaderId = leaderId;
        this.gitlabUrl = gitUrl;
        this.gitCredentialId = gitCredentialId;
    }

    public AppInfoEntity(Long id, Long projectId, String appName, String appCode, AppKindEnum appKind, String description, Long leaderId) {
        super(id);
        this.projectId = projectId;
        this.appName = appName;
        this.appCode = appCode;
        this.appKind = appKind;
        this.description = description;
        this.leaderId = leaderId;
    }

    public AppInfoEntity(Long projectId, String appCode, String appName) {
        this.projectId = projectId;
        this.appCode = appCode;
        this.appName = appName;
    }

    public AppInfoEntity(Long id, IsDeleted isDeleted) {
        super(id);
        this.isDeleted = isDeleted;
    }

    /**
     * 所属项目
     */
    @Column(name = "project_id")
    private Long projectId;

    /**
     * 应用名称
     */
    @Column(name = "app_name")
    private String appName;

    /**
     * 应用代码
     */
    @Column(name = "app_code")
    private String appCode;

    /**
     * 应用类型
     */
    @Column(name = "app_kind")
    private AppKindEnum appKind;

    /**
     * 应用描述
     */
    @Column(name = "description")
    private String description;

    /**
     * 负责人
     */
    @Column(name = "leader_id")
    private Long leaderId;

    /**
     * gitlab id
     */
    @Column(name = "gitlab_id")
    private Integer gitlabId;

    /**
     * gitlab url
     */
    @Column(name = "gitlab_url")
    private String gitlabUrl;

    /**
     * git credential id
     */
    @Column(name = "git_credential_id")
    private Long gitCredentialId;

    /**
     * 对应dcos的服务ID
     */
    @Column(name = "dcos_service_id")
    private String dcosServiceId;

    /**
     * docker 镜像名
     */
    @Column(name = "docker_image_name")
    private String dockerImageName;

    /**
     * 是否删除
     */
    @Column(name = "is_deleted")
    private IsDeleted isDeleted;

}