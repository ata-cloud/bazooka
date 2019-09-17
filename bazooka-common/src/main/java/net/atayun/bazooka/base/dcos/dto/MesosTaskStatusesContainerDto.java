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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年9月3日 17:00:00
 * @work mesos task statuses容器信息
 */
@Setter
@Getter
public class MesosTaskStatusesContainerDto implements Serializable {

    private static final long serialVersionUID = -5582647742872677016L;

    @JsonProperty(value = "container_id")
    @ApiModelProperty("容器id属性")
    private MesosTaskStatusesContainerIdDto containerId;
}
