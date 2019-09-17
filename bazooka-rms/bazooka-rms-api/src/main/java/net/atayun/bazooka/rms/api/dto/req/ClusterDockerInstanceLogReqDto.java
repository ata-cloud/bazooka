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
package net.atayun.bazooka.rms.api.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年8月19日 17:00:00
 * @work 实例日志请求dto
 */
@Setter
@Getter
@ApiModel("实例日志请求dto")
public class ClusterDockerInstanceLogReqDto implements Serializable {

    private static final long serialVersionUID = -1214592750061133622L;

    @ApiModelProperty("taskId")
    private String taskId;

    @ApiModelProperty("集群id")
    @NotNull
    private Long clusterId;

    @ApiModelProperty("marathon在mesos注册的framework id")
    private String frameworkId;

    @ApiModelProperty("slaveId")
    private String slaveId;

    @ApiModelProperty("容器id")
    private String containerId;

    @ApiModelProperty("日志类型: 0:标准日志 1:标准错误日志")
    private String logType;

    @ApiModelProperty("偏移量")
    private Integer offset;

    @ApiModelProperty("向上偏移量")
    private Integer upOffset;

    @ApiModelProperty("向下偏移量")
    private Integer downOffset;

    @ApiModelProperty("长度")
    private Integer length;
}
