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
package net.atayun.bazooka.rms.biz.service;

import net.atayun.bazooka.rms.api.dto.RmsDockerImageDto;
import net.atayun.bazooka.rms.api.dto.RmsDockerImagePushCheckResultDto;
import net.atayun.bazooka.rms.biz.dal.entity.RmsDockerImageEntity;
import com.youyu.common.service.IService;

/**
 * docker镜像
 *
 * @author 王宋军
 * @date 2019-07-22
 */
public interface RmsDockerImageService extends IService<RmsDockerImageDto, RmsDockerImageEntity> {

    /**
     * 删除镜像
     *
     * @param imageId
     */
    void deleteDockerImage(Long imageId);

    /**
     * 推送镜像到目标环境前的检查
     *
     * @param dockerImageDto
     * @param targetEnvId
     * @return
     */
    RmsDockerImagePushCheckResultDto pushImageToTargetEnvCheck(RmsDockerImageDto dockerImageDto, Long targetEnvId);

    /**
     * 复制镜像到另一个环境
     *
     * @param imageId
     * @param targetEnvId
     */
    void copyImage(Long imageId, Long targetEnvId);
}




