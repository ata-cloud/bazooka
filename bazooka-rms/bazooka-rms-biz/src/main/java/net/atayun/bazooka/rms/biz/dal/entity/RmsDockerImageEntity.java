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

import net.atayun.bazooka.rms.api.enums.DockerImageSource;
import com.youyu.common.enums.IsDeleted;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * docker镜像
 * <p>
 * 逻辑删除
 *
 * @author 王宋军
 * @date 2019-07-22
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rms_docker_image")
public class RmsDockerImageEntity extends com.youyu.common.entity.JdbcMysqlEntity<Long> {
    public RmsDockerImageEntity(String registry, String imageName, String imageTag, IsDeleted isDeleted) {
        this.registry = registry;
        this.imageName = imageName;
        this.imageTag = imageTag;
        this.isDeleted = isDeleted;
    }

    public RmsDockerImageEntity(Long envId, String imageName, String imageTag, IsDeleted isDeleted) {
        this.envId = envId;
        this.imageName = imageName;
        this.imageTag = imageTag;
        this.isDeleted = isDeleted;
    }

    public RmsDockerImageEntity(Long envId, String imageName, IsDeleted isDeleted) {
        this.envId = envId;
        this.imageName = imageName;
        this.isDeleted = isDeleted;
    }

    public RmsDockerImageEntity(Long envId, Long appId, IsDeleted isDeleted) {
        this.envId = envId;
        this.appId = appId;
        this.isDeleted = isDeleted;
    }

    public RmsDockerImageEntity(Long id, IsDeleted isDeleted) {
        super(id);
        this.isDeleted = isDeleted;
    }

    public RmsDockerImageEntity(IsDeleted isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * 环境ID
     */
    @Column(name = "env_id")
    private Long envId;

    /**
     * 应用ID
     */
    @Column(name = "app_id")
    private Long appId;

    /**
     * 镜像名称
     */
    @Column(name = "image_name")
    private String imageName;

    /**
     * 镜像tag
     */
    @Column(name = "image_tag")
    private String imageTag;

    /**
     * 镜像库
     */
    @Column(name = "registry")
    private String registry;

    /**
     * 代码库地址
     */
    @Column(name = "git_url")
    private String gitUrl;

    /**
     * 代码库分支
     */
    @Column(name = "git_branch")
    private String gitBranch;

    /**
     * 代码提交ID
     */
    @Column(name = "git_commit_id")
    private String gitCommitId;

    /**
     * 代码提交备注
     */
    @Column(name = "git_commit_mgs")
    private String gitCommitMgs;

    /**
     * 代码提交时间
     */
    @Column(name = "git_commit_time")
    private LocalDateTime gitCommitTime;

    /**
     * 镜像来源
     */
    @Column(name = "source")
    private DockerImageSource source;

    /**
     * 构建或推送的时间
     */
    @Column(name = "image_create_time")
    private LocalDateTime imageCreateTime;

    /**
     * 是否删除
     */
    @Column(name = "is_deleted")
    private IsDeleted isDeleted;
}