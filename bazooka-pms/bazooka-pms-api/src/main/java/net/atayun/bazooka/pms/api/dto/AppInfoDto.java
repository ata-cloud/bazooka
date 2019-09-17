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

import net.atayun.bazooka.pms.api.enums.AppKindEnum;
import net.atayun.bazooka.pms.api.enums.UserTypeEnum;
import com.youyu.common.dto.BaseDto;
import com.youyu.common.enums.IsDeleted;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2019-07-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class AppInfoDto extends BaseDto<Long> {

    @ApiModelProperty(value = "项目ID")
    private Long projectId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "应用代码")
    private String appCode;

    @ApiModelProperty(value = "置顶，排序")
    private int orderId;

    @ApiModelProperty(value = "用户角色类型")
    private UserTypeEnum userType;

    @ApiModelProperty(value = "应用类型")
    private AppKindEnum appKind;

    @ApiModelProperty(value = "应用描述")
    private String description;

    @ApiModelProperty(value = "负责人")
    private Long leaderId;

    @ApiModelProperty(value = "gitlab id")
    private Integer gitlabId;

    @ApiModelProperty(value = "gitlab url")
    private String gitlabUrl;

    /**
     * git credential id
     */
    @ApiModelProperty(value = "git credential id")
    private Long gitCredentialId;

    @ApiModelProperty(value = "对应dcos的服务ID")
    private String dcosServiceId;

    @ApiModelProperty(value = "docker 镜像名")
    private String dockerImageName;

    @ApiModelProperty(value = "是否删除")
    private IsDeleted isDeleted;

}

