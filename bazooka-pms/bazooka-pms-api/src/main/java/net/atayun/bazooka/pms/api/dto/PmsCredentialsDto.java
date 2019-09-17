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

import net.atayun.bazooka.pms.api.enums.CredentialDomainEnum;
import net.atayun.bazooka.pms.api.enums.CredentialScopeEnum;
import net.atayun.bazooka.pms.api.enums.CredentialTypeEnum;
import com.youyu.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 凭据管理
 *
 * @author WangSongJun
 * @date 2019-08-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class PmsCredentialsDto extends BaseDto<Long> {
    /**
     * 凭据域
     */
    @NotNull(message = "凭据域不可空")
    @ApiModelProperty(value = "凭据域")
    private CredentialDomainEnum domain;
    /**
     * 使用范围
     */
    @NotNull(message = "使用范围不可空")
    @ApiModelProperty(value = "使用范围(默认：GLOBAL)")
    private CredentialScopeEnum scope;
    /**
     * 凭据名称
     */
    @Size(max = 30, message = "凭据名称长度不能超过30")
    @NotBlank(message = "凭据名称不可空")
    @ApiModelProperty(value = "凭据名称")
    private String credentialName;
    /**
     * 凭据类型
     */
    @NotNull(message = "凭据类型不可空")
    @ApiModelProperty(value = "凭据类型")
    private CredentialTypeEnum credentialType;
    /**
     * 凭据key
     */
    @Size(max = 64, message = "凭据key长度不能超过64")
    @NotBlank(message = "凭据key不可空")
    @ApiModelProperty(value = "凭据key")
    private String credentialKey;
    /**
     * 凭据value
     */
    @NotBlank(message = "凭据value不可空")
    @Size(max = 256, message = "凭据value长度不能超过256")
    @ApiModelProperty(value = "凭据value")
    private String credentialValue;
}
