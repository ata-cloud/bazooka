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
package net.atayun.bazooka.rms.biz.service.impl;

import net.atayun.bazooka.base.docker.DockerRegistryService;
import net.atayun.bazooka.rms.api.dto.ClusterConfigDto;
import net.atayun.bazooka.rms.api.dto.RmsDockerImageDto;
import net.atayun.bazooka.rms.api.dto.RmsDockerImagePushCheckResultDto;
import net.atayun.bazooka.rms.api.enums.DockerImageSource;
import net.atayun.bazooka.rms.biz.dal.dao.RmsDockerImageMapper;
import net.atayun.bazooka.rms.biz.dal.entity.RmsDockerImageEntity;
import net.atayun.bazooka.rms.biz.service.EnvService;
import net.atayun.bazooka.rms.biz.service.RmsDockerImageService;
import com.youyu.common.enums.IsDeleted;
import com.youyu.common.service.AbstractService;
import com.youyu.common.utils.YyAssert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;


/**
 * docker镜像
 *
 * @author 王宋军
 * @date 2019-07-22
 */
@Slf4j
@Service
public class RmsDockerImageServiceImpl extends AbstractService<Long, RmsDockerImageDto, RmsDockerImageEntity, RmsDockerImageMapper> implements RmsDockerImageService {
    @Autowired
    private EnvService envService;
    @Autowired
    private DockerRegistryService registryService;

    /**
     * 删除镜像
     *
     * @param imageId
     */
    @Override
    public void deleteDockerImage(Long imageId) {
        RmsDockerImageDto dockerImageDto = this.selectOne(new RmsDockerImageEntity(imageId, IsDeleted.NOT_DELETED));
        YyAssert.paramCheck(ObjectUtils.isEmpty(dockerImageDto), "镜像不存在或已删除！");

        this.registryService.deleteTag(dockerImageDto.getRegistry(), dockerImageDto.getImageName(), dockerImageDto.getImageTag());

        this.updateByPrimaryKeySelective(new RmsDockerImageEntity(imageId, IsDeleted.DELETED));
    }

    /**
     * 推送镜像到目标环境
     * <p>
     * 目标环境不能与当前镜像环境相同
     * 目标环境没有当前镜像相同的名称和tag
     *
     * @param dockerImageDto
     * @param targetEnvId
     * @return
     */
    @Override
    public RmsDockerImagePushCheckResultDto pushImageToTargetEnvCheck(RmsDockerImageDto dockerImageDto, Long targetEnvId) {
        log.info("推送镜像到目标环境检查 [{} -> {}]", dockerImageDto.getImageName(), targetEnvId);
        YyAssert.paramCheck(targetEnvId.equals(dockerImageDto.getEnvId()), "不能推送到当前环境！");

        ClusterConfigDto targetCluster = this.envService.getClusterConfiguration(targetEnvId);
        YyAssert.paramCheck(ObjectUtils.isEmpty(targetCluster), "目标集群不存在！");

        int exist = this.selectCount(new RmsDockerImageEntity(targetEnvId, dockerImageDto.getImageName(), dockerImageDto.getImageTag(), IsDeleted.NOT_DELETED));
        YyAssert.paramCheck(exist > 0, "目标环境已存在相同的镜像的tag版本！");

        ClusterConfigDto sourceCluster = this.envService.getClusterConfiguration(dockerImageDto.getEnvId());
        if (!sourceCluster.getDockerHubUrl().endsWith(targetCluster.getDockerHubUrl())) {
            return new RmsDockerImagePushCheckResultDto(false, targetEnvId, targetCluster.getDockerHubUrl(), dockerImageDto);
        } else {
            return new RmsDockerImagePushCheckResultDto(true, targetEnvId, targetCluster.getDockerHubUrl(), dockerImageDto);
        }
    }

    /**
     * 复制镜像到另一个环境
     *
     * @param imageId
     * @param targetEnvId
     */
    @Override
    public void copyImage(Long imageId, Long targetEnvId) {
        RmsDockerImageDto dockerImageDto = this.selectByPrimaryKey(imageId);
        YyAssert.paramCheck(ObjectUtils.isEmpty(dockerImageDto)||IsDeleted.DELETED.equals(dockerImageDto.getIsDeleted()), "原镜像不存在或已删除！");

        RmsDockerImagePushCheckResultDto pushCheckResultDto = this.pushImageToTargetEnvCheck(dockerImageDto, targetEnvId);

        dockerImageDto.setId(null);
        dockerImageDto.setEnvId(targetEnvId);
        dockerImageDto.setSource(DockerImageSource.Push);
        dockerImageDto.setImageCreateTime(LocalDateTime.now());

        if (!pushCheckResultDto.isSameRegistry()) {
            dockerImageDto.setRegistry(pushCheckResultDto.getTargetRegistry());
        }
        this.insertSelective(dockerImageDto);
    }
}




