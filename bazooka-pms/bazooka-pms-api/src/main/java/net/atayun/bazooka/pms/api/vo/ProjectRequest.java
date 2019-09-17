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


import net.atayun.bazooka.pms.api.dto.DevUserInfo;
import net.atayun.bazooka.pms.api.dto.EnvDto;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author rache
 * @date 2019-07-12
 */
@Data
public class ProjectRequest {
    /**
     * 项目id
     */
    private Long projectId;
    /**
     * 项目名称
     */
    @NotNull
    @Size(max = 20,message = "项目名称过长，不能超过20")
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5\\-]*$",message = "项目名称必须中文，字母，数字，中横线")
    private String projectName;
    /**
     * 项目code
     */
    @NotNull
    @Size(max = 20,message = "项目code过长，不能超过20")
    @Pattern(regexp = "^(([a-z0-9]|[a-z0-9][a-z0-9\\-]*[a-z0-9])\\.)*([a-z0-9]|[a-z0-9][a-z0-9\\-]*[a-z0-9])$",message = "项目code必须字母，数字，中横线")
    private String projectCode;
    /**
     * 项目描述
     */
    @Size(max = 500,message = "描述过长，不能超过500")
    private String description;
    /**
     * 项目负责人id
     */
    private Long masterUserId;

    private List<DevUserInfo> devUserIds;
    /**
     * 项目关联环境相关
     */

    private List<EnvDto> envList;
}
