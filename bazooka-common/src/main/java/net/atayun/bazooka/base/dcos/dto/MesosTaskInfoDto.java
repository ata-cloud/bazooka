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
import java.util.ArrayList;
import java.util.List;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work mesos task详细信息
 */
@Setter
@Getter
public class MesosTaskInfoDto implements Serializable {

    private static final long serialVersionUID = 1150393571883423083L;

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("名称:(格式:服务code.项目code.环境code)")
    private String name;

    @JsonProperty(value = "framework_id")
    @ApiModelProperty("marathon在mesos注册的framework id")
    private String frameworkId;

    @JsonProperty(value = "slave_id")
    @ApiModelProperty(value = "宿主机code,不是ip", example = "fb384ba5-1a1c-4c3e-9d8b-5d9813001a06-S2")
    private String slaveId;

    @ApiModelProperty("状态:(TASK_STAGING;TASK_STARTING;TASK_RUNNING;TASK_UNREACHABLE;TASK_KILLING;TASK_FINISHED;TASK_KILLED;TASK_FAILED;TASK_LOST)")
    private String state;

    @ApiModelProperty("资源")
    private MesosTaskResourcesDto resources;

    @ApiModelProperty("发布状态历史:只保留时间")
    private List<MesosTaskStatusesDto> statuses = new ArrayList<>();

    @ApiModelProperty("容器")
    private MesosTaskContainerDto container;

}
