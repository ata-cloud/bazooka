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
package net.atayun.bazooka.pms.api.dto;

import net.atayun.bazooka.pms.api.enums.DockerImageSource;
import com.youyu.common.dto.BaseDto;
import com.youyu.common.enums.IsDeleted;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * docker镜像
 *
 * @author 王宋军
 * @date 2019-07-22
 */
@Data
@ApiModel
public class RmsDockerImageDto extends BaseDto<Long>{

    @ApiModelProperty(value = "环境ID")
    private Long envId;

    @ApiModelProperty(value = "应用ID")
    private Long appId;

    @ApiModelProperty(value = "镜像名称")
    private String imageName;

    @ApiModelProperty(value = "镜像tag")
    private String imageTag;

    @ApiModelProperty(value = "镜像库")
    private String registry;

    @ApiModelProperty(value = "代码库地址")
    private String gitUrl;

    @ApiModelProperty(value = "代码库分支")
    private String gitBranch;

    @ApiModelProperty(value = "代码提交ID")
    private String gitCommitId;

    @ApiModelProperty(value = "代码提交备注")
    private String gitCommitMgs;

    @ApiModelProperty(value = "代码提交时间")
    private LocalDateTime gitCommitTime;

    @ApiModelProperty(value = "镜像来源")
    private DockerImageSource source;

    @ApiModelProperty(value = "构建或推送的时间")
    private LocalDateTime imageCreateTime;

    @ApiModelProperty(value = "是否删除")
    private IsDeleted isDeleted;

}

