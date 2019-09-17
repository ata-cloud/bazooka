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
import java.util.List;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月18日 17:00:00
 * @work 容器task详细信息
 */
@Setter
@Getter
public class TaskInfoDto implements Serializable {

    private static final long serialVersionUID = 3764101572558848812L;

    @ApiModelProperty("task id")
    private String id;

    @ApiModelProperty("appId")
    private String appId;

    @ApiModelProperty("主机地址")
    private String host;

    @ApiModelProperty("端口列表")
    private List<Integer> ports;

    @ApiModelProperty("容器状态:stage、running、killing、killed")
    private String state;

    @ApiModelProperty("版本")
    private String version;

    @ApiModelProperty("容器启动时间")
    private String startedAt;

    @ApiModelProperty("健康检查列表")
    private List<TaskHealthDto> healthCheckResults;
}
