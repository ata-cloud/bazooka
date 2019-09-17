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

import net.atayun.bazooka.base.enums.deploy.AppOperationEnum;
import com.youyu.common.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import lombok.NoArgsConstructor;


/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2019-07-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class PmsAppDeployStatusDto extends BaseDto<Long>{

    @ApiModelProperty(value = "应用ID")
    private Long appId;

    @ApiModelProperty(value = "环境ID")
    private Long envId;

    @ApiModelProperty(value = "部署中")
    private Boolean deploying;

    @ApiModelProperty(value = "部署类型")
    private AppOperationEnum deployType;

}

