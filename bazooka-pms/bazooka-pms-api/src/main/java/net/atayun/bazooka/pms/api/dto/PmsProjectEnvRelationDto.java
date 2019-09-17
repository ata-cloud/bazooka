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

import com.youyu.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2019-07-26
 */
@Data
@ApiModel
public class PmsProjectEnvRelationDto extends BaseDto<Long>{

    @ApiModelProperty(value = "")
    private Integer projectId;

    @ApiModelProperty(value = "")
    private Integer envId;

    @ApiModelProperty(value = "开始端口")
    private Integer portStart;

    @ApiModelProperty(value = "结束端口")
    private Integer portEnd;

    @ApiModelProperty(value = "产生者")
    private String createAuthor;

    @ApiModelProperty(value = "更新者:")
    private String updateAuthor;

    @ApiModelProperty(value = "更新时间:")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "产生时间:")
    private LocalDateTime createTime;

}

