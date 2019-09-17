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
package net.atayun.bazooka.rms.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 集群容器Dto
 */
@Setter
@Getter
@ApiModel("集群容器Dto")
public class ClusterDockerDto implements Serializable {

    private static final long serialVersionUID = 7289199944109965748L;

    @ApiModelProperty("taskId")
    private String taskId;

    @ApiModelProperty("集群id")
    private Long clusterId;

    @ApiModelProperty(value = "宿主机code,不是ip", example = "fb384ba5-1a1c-4c3e-9d8b-5d9813001a06-S2")
    private String slaveId;

    @ApiModelProperty("容器id")
    private String containerId;

    @ApiModelProperty("marathon在mesos注册的framework id")
    private String frameworkId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("状态:(TASK_STAGING;TASK_STARTING;TASK_RUNNING;TASK_UNREACHABLE;TASK_KILLING;TASK_FINISHED;TASK_KILLED;TASK_FAILED;TASK_LOST)")
    private String status;

    @ApiModelProperty("宿主机")
    private String host;

    @ApiModelProperty("宿主机映射端口")
    private List<String> ports;

    @ApiModelProperty("cpu")
    private BigDecimal cpu;

    @ApiModelProperty("内存")
    private BigDecimal memory;

    @ApiModelProperty("健康状态(0:健康 1:不健康 2:没有健康检查 3:不存在健康)")
    private String healthStatus;
}
