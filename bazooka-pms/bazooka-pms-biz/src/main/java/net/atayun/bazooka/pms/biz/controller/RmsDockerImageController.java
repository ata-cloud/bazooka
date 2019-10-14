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
package net.atayun.bazooka.pms.biz.controller;


import net.atayun.bazooka.pms.api.RmsDockerImageApi;
import net.atayun.bazooka.pms.api.dto.RmsDockerImageDto;
import net.atayun.bazooka.pms.api.dto.RmsDockerImagePushCheckResultDto;
import net.atayun.bazooka.pms.biz.dal.entity.RmsDockerImageEntity;
import net.atayun.bazooka.pms.biz.service.RmsDockerImageService;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import com.youyu.common.enums.IsDeleted;
import com.youyu.common.exception.BizException;
import com.youyu.common.utils.YyAssert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import static com.youyu.common.enums.BaseResultCode.NO_DATA_FOUND;


/**
 * docker镜像
 *
 * @author 王宋军
 * @date 2019-07-22
 */
@Api(description = "镜像信息")
@RestController
@RequestMapping("docker-image")
public class RmsDockerImageController implements RmsDockerImageApi {

    @Autowired
    private RmsDockerImageService rmsDockerImageService;

    @Override
    @ApiOperation(value = "查询镜像信息")
    @GetMapping("/get/{id:\\d+}")
    public Result<RmsDockerImageDto> get(@PathVariable Long id) {
        RmsDockerImageDto dto = rmsDockerImageService.selectByPrimaryKey(id);
        if (dto == null) {
            return Result.fail(NO_DATA_FOUND);
        }
        return Result.ok(dto);
    }

    /**
     * select one
     *
     * @param registry
     * @param imageName
     * @param imageTag
     * @return
     */
    @Override
    @GetMapping("/get-image-info")
    public Result<RmsDockerImageDto> getDockerImageInfoByImageName(@RequestParam String registry, @RequestParam String imageName, @RequestParam String imageTag) {
        Example example = new Example(RmsDockerImageEntity.class);
        example.createCriteria()
                .andEqualTo("imageName", imageName)
                .andEqualTo("imageTag", imageTag)
                .andLike("registry", MessageFormat.format("%{0}%", registry));
        List<RmsDockerImageDto> imageDtoList = this.rmsDockerImageService.selectByExample(example);

        if (ObjectUtils.isEmpty(imageDtoList)) {
            return Result.ok();
        } else {
            return Result.ok(imageDtoList.get(0));
        }
    }

    /**
     * select image list
     *
     * @param registry
     * @param imageName
     * @return
     */
    @Override
    @GetMapping("/get-image-list")
    public Result<List<RmsDockerImageDto>> getDockerImageListByRegistryAndImageName(@RequestParam String registry, @RequestParam String imageName) {
        Example example = new Example(RmsDockerImageEntity.class);
        example.createCriteria()
                .andEqualTo("imageName", imageName)
                .andLike("registry", MessageFormat.format("%{0}%", registry));

        List<RmsDockerImageDto> imageDtoList = this.rmsDockerImageService.selectByExample(example);

        return Result.ok(imageDtoList);
    }

    @Override
    @ApiOperation(value = "删除镜像")
    @PostMapping("/delete/{id:\\d+}")
    public Result delete(@PathVariable(name = "id") Long id) {
        this.rmsDockerImageService.deleteDockerImage(id);
        return Result.ok();
    }

    @Override
    @ApiOperation(value = "创建镜像")
    @PostMapping("/create")
    public Result<RmsDockerImageDto> create(@RequestBody RmsDockerImageDto dto) {
        int count = rmsDockerImageService.insertSelective(dto);
        if (count <= 0) {
            return Result.fail(NO_DATA_FOUND);
        }
        return Result.ok(dto);
    }

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
    @Override
    @ApiOperation("是否需要进行镜像推送操作")
    @GetMapping("image-push-pre-check/{imageId:\\d+}/{targetEnvId:\\d+}")
    public Result<RmsDockerImagePushCheckResultDto> imagePushPreCheck(@PathVariable Long imageId, @PathVariable Long targetEnvId) {
        RmsDockerImageDto dockerImageDto = this.rmsDockerImageService.selectByPrimaryKey(imageId);

        YyAssert.paramCheck(ObjectUtils.isEmpty(dockerImageDto) || IsDeleted.DELETED.equals(dockerImageDto.getIsDeleted()), "原镜像不存在或已删除！");

        return Result.ok(this.rmsDockerImageService.pushImageToTargetEnvCheck(dockerImageDto, targetEnvId));
    }

    @Override
    @ApiOperation("复制镜像")
    @GetMapping("copy-image/{imageId:\\d+}/{targetEnvId:\\d+}")
    public Result imageCopy(@PathVariable Long imageId, @PathVariable Long targetEnvId) {
        this.rmsDockerImageService.copyImage(imageId, targetEnvId);
        return Result.ok();
    }

    @ApiOperation("查询镜像tag列表")
    @GetMapping("/get-tag-list/{envId:\\d+}/{imageName}")
    public Result<List<String>> getTagListByImageNameAndEnv(@PathVariable Long envId, @PathVariable String imageName) {
        List<RmsDockerImageDto> rmsDockerImageDtoList = this.rmsDockerImageService.select(new RmsDockerImageEntity(envId, imageName, IsDeleted.NOT_DELETED));

        return Result.ok(this.getTagListByDtoList(rmsDockerImageDtoList));
    }


    @ApiOperation("查询镜像tag列表")
    @GetMapping("/get-tag-list/{envId:\\d+}/{appId:\\d+}")
    public Result<List<String>> getTagListByImageNameAndAppId(@PathVariable Long envId, @PathVariable Long appId) {
        List<RmsDockerImageDto> rmsDockerImageDtoList = this.rmsDockerImageService.select(new RmsDockerImageEntity(envId, appId, IsDeleted.NOT_DELETED));

        return Result.ok(this.getTagListByDtoList(rmsDockerImageDtoList));
    }

    private List<String> getTagListByDtoList(List<RmsDockerImageDto> rmsDockerImageDtoList) {
        if (ObjectUtils.isEmpty(rmsDockerImageDtoList)) {
            throw new BizException(NO_DATA_FOUND);
        }

        return rmsDockerImageDtoList.stream()
                .map(rmsDockerImageDto -> rmsDockerImageDto.getImageTag())
                .collect(Collectors.toList());
    }


    @ApiOperation("查询镜像列表")
    @GetMapping("/list/{appId:\\d+}/{envId:\\d+}")
    public Result<List<RmsDockerImageDto>> dockerImageList(@PathVariable Long appId, @PathVariable Long envId) {
        Example example = this.selectImageListOrderByCreateTimeExample(appId, envId, IsDeleted.NOT_DELETED);
        return Result.ok(this.rmsDockerImageService.selectByExample(example));
    }

    @ApiOperation("分页查询镜像列表")
    @GetMapping("/list/{appId:\\d+}/{envId:\\d+}/{page}/{size}")
    public Result<PageData<RmsDockerImageDto>> getAppConfigListByPage(@PathVariable Long appId, @PathVariable Long envId, @PathVariable Integer page, @PathVariable Integer size) {
        //分页条件查询
        PageData<RmsDockerImageDto> pageData = new PageData<>(page, size);
        Example example = this.selectImageListOrderByCreateTimeExample(appId, envId, IsDeleted.NOT_DELETED);
        List<RmsDockerImageDto> dockerImageDtoList = this.rmsDockerImageService.selectByExampleInPage(example, pageData);
        pageData.setRows(dockerImageDtoList);
        return Result.ok(pageData);
    }

    private Example selectImageListOrderByCreateTimeExample(Long appId, Long envId, IsDeleted isDeleted) {
        Example example = new Example(RmsDockerImageEntity.class);
        example.orderBy("createTime").desc();
        example.createCriteria()
                .andEqualTo("isDeleted", IsDeleted.NOT_DELETED)
                .andEqualTo("appId", appId)
                .andEqualTo("envId", envId);
        return example;
    }
}
