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

import net.atayun.bazooka.pms.api.enums.CredentialTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author WangSongJun
 * @date 2019-08-27
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class AppInfoWithCredential extends AppInfoDto {
    public AppInfoWithCredential(AppInfoDto appInfoDto, PmsCredentialsDto credentialsDto) {
        this(appInfoDto, credentialsDto.getCredentialType(), credentialsDto.getCredentialKey(), credentialsDto.getCredentialValue());
    }

    public AppInfoWithCredential(AppInfoDto appInfoDto, CredentialTypeEnum credentialType, String credentialKey, String credentialValue) {
        super(
                appInfoDto.getProjectId(),
                appInfoDto.getProjectName(),
                appInfoDto.getAppName(),
                appInfoDto.getAppCode(),
                appInfoDto.getOrderId(),
                appInfoDto.getUserType(),
                appInfoDto.getAppKind(),
                appInfoDto.getDescription(),
                appInfoDto.getLeaderId(),
                appInfoDto.getGitlabId(),
                appInfoDto.getGitlabUrl(),
                appInfoDto.getGitCredentialId(),
                appInfoDto.getDcosServiceId(),
                appInfoDto.getDockerImageName(),
                appInfoDto.getIsDeleted()
        );
        this.credentialType = credentialType;
        this.credentialKey = credentialKey;
        this.credentialValue = credentialValue;
    }

    /**
     * 凭据类型
     */
    @ApiModelProperty(value = "凭据类型")
    private CredentialTypeEnum credentialType;
    /**
     * 凭据key
     */
    @ApiModelProperty(value = "凭据key")
    private String credentialKey;
    /**
     * 凭据value
     */
    @ApiModelProperty(value = "凭据value")
    private String credentialValue;
}
