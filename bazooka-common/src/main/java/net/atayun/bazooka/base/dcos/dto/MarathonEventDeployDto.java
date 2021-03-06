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
package net.atayun.bazooka.base.dcos.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月29日 17:00:00
 * @work marathon event deploy转换dto
 */
@Setter
@Getter
public class MarathonEventDeployDto implements Serializable {

    private static final long serialVersionUID = -2517056460212750735L;

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("事件类型")
    private String eventType;

    @ApiModelProperty("版本信息")
    private MarathonEventDeployVersionDto plan;
}
