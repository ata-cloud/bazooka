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
package net.atayun.bazooka.rms.api.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 镜像推送检查结果
 *
 * @author WangSongJun
 * @date 2019-08-14
 */
@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class RmsDockerImagePushCheckResultDto {
    /**
     * 是否相同的镜像库
     */
    private boolean sameRegistry;

    private Long targetEnvId;

    private String targetRegistry;

    /**
     * 镜像信息
     */
    private RmsDockerImageDto dockerImageDto;

}
