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
package net.atayun.bazooka.deploy.biz.v2.param;

import com.youyu.common.api.PageData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import net.atayun.bazooka.deploy.biz.v2.dto.app.AppOptHisPlatformDto;

/**
 * @author Ping
 * @date 2019-08-05
 */
@Getter
@Setter
@ApiModel(description = "操作历史Marathon相关参数")
public class AppOptHisPlatformParam extends PageData<AppOptHisPlatformDto> {

    @ApiModelProperty("服务Id")
    private Long appId;

    @ApiModelProperty("环境Id")
    private Long envId;
}
