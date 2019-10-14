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
package net.atayun.bazooka.pms.api;


import net.atayun.bazooka.pms.api.dto.RmsDockerImageDto;
import net.atayun.bazooka.pms.api.dto.RmsDockerImagePushCheckResultDto;
import com.youyu.common.api.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2019-07-22
 */
@RequestMapping(path = "/docker-image")
public interface RmsDockerImageApi {

    /**
     * select one
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    Result<RmsDockerImageDto> get(@PathVariable(name = "id") Long id);

    /**
     * select one
     *
     * @param registry
     * @param imageName
     * @param imageTag
     * @return
     */
    @GetMapping("/get-image-info")
    Result<RmsDockerImageDto> getDockerImageInfoByImageName(@RequestParam String registry, @RequestParam String imageName, @RequestParam String imageTag);


    /**
     * select image list
     *
     * @param registry
     * @param imageName
     * @return
     */
    @GetMapping("/get-image-list")
    Result<List<RmsDockerImageDto>> getDockerImageListByRegistryAndImageName(@RequestParam String registry, @RequestParam String imageName);

    /**
     * save one
     *
     * @param dto
     * @return
     */
    @PostMapping("/create")
    Result<RmsDockerImageDto> create(@RequestBody RmsDockerImageDto dto);

    /**
     * 镜像推送预检
     * <p>
     * 是否需要进行镜像推送操作
     * 是否相同镜像库
     * 是否已存在
     *
     * @param imageId
     * @param targetEnvId
     * @return
     */
    @GetMapping("image-push-pre-check/{imageId:\\d+}/{targetEnvId:\\d+}")
    Result<RmsDockerImagePushCheckResultDto> imagePushPreCheck(@PathVariable Long imageId, @PathVariable Long targetEnvId);

    /**
     * 复制镜像
     *
     * @param imageId
     * @param targetEnvId
     * @return
     */
    @ApiOperation("复制镜像")
    @GetMapping("copy-image/{imageId:\\d+}/{targetEnvId:\\d+}")
    Result imageCopy(@PathVariable Long imageId, @PathVariable Long targetEnvId);

    /**
     * 删除镜像
     *
     * @param imageId
     * @return
     */
    @ApiOperation("删除镜像")
    @GetMapping("delete/{imageId:\\d+}")
    Result delete(@PathVariable Long imageId);

}
