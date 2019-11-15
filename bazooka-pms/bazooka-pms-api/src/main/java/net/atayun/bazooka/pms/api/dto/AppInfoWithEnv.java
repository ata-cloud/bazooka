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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.atayun.bazooka.pms.api.enums.CredentialTypeEnum;
import net.atayun.bazooka.pms.api.vo.AppEnvInfoVo;

import java.util.List;

/**
 * @author WangSongJun
 * @date 2019-08-27
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class AppInfoWithEnv extends AppInfoDto {


    public AppInfoWithEnv(AppInfoDto appInfoDto, List<AppEnvInfoVo> appEnvInfoVoList) {
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
        this.appEnvInfoVoList = appEnvInfoVoList;
    }

    /**
     * 所在环境信息
     */
    @ApiModelProperty(value = "所在环境信息")
    List<AppEnvInfoVo> appEnvInfoVoList;


}
