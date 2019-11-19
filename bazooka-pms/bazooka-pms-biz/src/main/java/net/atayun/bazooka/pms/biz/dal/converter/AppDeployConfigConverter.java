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
package net.atayun.bazooka.pms.biz.dal.converter;

import com.google.gson.reflect.TypeToken;
import com.youyu.common.enums.IsDeleted;
import net.atayun.bazooka.base.utils.JsonUtil;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.param.HealthCheck;
import net.atayun.bazooka.pms.api.param.PortMapping;
import net.atayun.bazooka.pms.api.param.VolumeMount;
import net.atayun.bazooka.pms.biz.dal.entity.AppDeployConfigEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author WangSongJun
 * @date 2019-07-17
 */
public class AppDeployConfigConverter {

    /**
     * dto to entity
     *
     * @param source
     * @return
     */
    public static AppDeployConfigEntity appDeployConfigEntity(AppDeployConfigDto source) {
        return ObjectUtils.isEmpty(source)
                ? null
                : new AppDeployConfigEntity(
                source.getId(),
                source.getAppId(),
                source.getEnvId(),
                source.getConfigName(),
                source.getConfigDescription(),
                source.getDeployMode(),
                source.getGitBranchAllow(),
                source.getGitBranchDeny(),
                source.getCompileCommand(),
                source.getDockerfilePath(),
                source.getCpus(),
                source.getMemory(),
                source.getDisk(),
                source.getInstance(),
                source.getStartCommand(),
                ObjectUtils.isEmpty(source.getPortMappings()) ? null : JsonUtil.toJson(source.getPortMappings()),
                ObjectUtils.isEmpty(source.getEnvironmentVariable()) ? null : JsonUtil.toJson(source.getEnvironmentVariable()),
                ObjectUtils.isEmpty(source.getVolumes()) ? null : JsonUtil.toJson(source.getVolumes()),
                ObjectUtils.isEmpty(source.getHealthChecks()) ? null : JsonUtil.toJson(source.getHealthChecks()),
                IsDeleted.NOT_DELETED,
                ObjectUtils.isEmpty(source.getClusterNodes()) ? null : JsonUtil.toJson(source.getClusterNodes())
        );
    }

    /**
     * entity to dto
     *
     * @param source
     * @return
     */
    public static AppDeployConfigDto appDeployConfigDto(AppDeployConfigEntity source) {
        return ObjectUtils.isEmpty(source)
                ? null
                : new AppDeployConfigDto(
                source.getId(),
                source.getAppId(),
                source.getEnvId(),
                null,
                source.getConfigName(),
                source.getConfigDescription(),
                source.getDeployMode(),
                source.getGitBranchAllow(),
                source.getGitBranchDeny(),
                source.getCompileCommand(),
                source.getDockerfilePath(),
                source.getCpus(),
                source.getMemory(),
                source.getDisk(),
                source.getInstance(),
                source.getStartCommand(),
                StringUtils.hasText(source.getClusterNodes()) ? JsonUtil.fromJson(source.getClusterNodes(),
                        new TypeToken<List<Long>>() {
                        }.getType()) : null,
                StringUtils.hasText(source.getPortMappings()) ? JsonUtil.fromJson(source.getPortMappings(),
                        new TypeToken<List<PortMapping>>() {
                        }.getType()) : null,
                StringUtils.hasText(source.getEnvironmentVariable()) ? JsonUtil.fromJson(source.getEnvironmentVariable(),
                        new TypeToken<Map<String, Object>>() {
                        }.getType()) : null,
                StringUtils.hasText(source.getVolumes()) ? JsonUtil.fromJson(source.getVolumes(),
                        new TypeToken<List<VolumeMount>>() {
                        }.getType()) : null,
                StringUtils.hasText(source.getHealthChecks()) ? JsonUtil.fromJson(source.getHealthChecks(),
                        new TypeToken<List<HealthCheck>>() {
                        }.getType()) : null,
                source.getUpdateAuthor(),
                source.getUpdateTime()
        );
    }

}
