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
package net.atayun.bazooka.upms.api.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 用户修改密码请求req
 */
@ApiModel("用户修改密码请求req")
@Setter
@Getter
public class UserModifyPasswordReqDTO implements Serializable {

    private static final long serialVersionUID = -9070674969048788741L;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("原始密码")
    @NotNull
    private String originPassword;

    @ApiModelProperty("密码")
    @NotNull
    private String password;
}
