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
package net.atayun.bazooka.pms.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

/**
 * @author WangSongJun
 * @date 2019-08-29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class CredentialsUpdateParam {
    /**
     * 凭据key
     */
    @Size(max = 64, message = "凭据key长度不能超过64")
    @ApiModelProperty(value = "凭据key")
    private String credentialKey;
    /**
     * 凭据value
     */
    @Size(max = 256, message = "凭据value长度不能超过256")
    @ApiModelProperty(value = "凭据value")
    private String credentialValue;
}
