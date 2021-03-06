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
package net.atayun.bazooka.rms.api.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Positive;

/**
 * @Author: hanxiaorui
 * @Date: 2019/7/16 17:54
 * @Description: 环境MLB端口信息请求
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EnvMlbPortUsedReq {

    @ApiModelProperty("环境编号")
    @Positive(message = "环境编号不能为空")
    private Long envId;

    @ApiModelProperty("服务编号")
    @Positive(message = "端口号不能为空")
    private int port;
}
