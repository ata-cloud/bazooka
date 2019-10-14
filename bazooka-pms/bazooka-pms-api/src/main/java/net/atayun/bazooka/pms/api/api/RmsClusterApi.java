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
package net.atayun.bazooka.pms.api.api;

import net.atayun.bazooka.pms.api.dto.ClusterAppResourceDto;
import net.atayun.bazooka.pms.api.dto.req.ClusterDetailReqDto;
import net.atayun.bazooka.pms.api.dto.req.ClusterDockerInstanceLogReqDto;
import net.atayun.bazooka.pms.api.dto.req.ClusterReqDto;
import net.atayun.bazooka.pms.api.dto.rsp.ClusterDetailRspDto;
import net.atayun.bazooka.pms.api.dto.rsp.ClusterDockerInstanceLogRspDto;
import net.atayun.bazooka.pms.api.dto.rsp.ClusterMarathonConfigRspDto;
import net.atayun.bazooka.pms.api.dto.rsp.ClusterRspDto;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;


/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 资源集群 Api
 */
@RequestMapping("/cluster")
public interface RmsClusterApi {

    /**
     * 根据集群请求参数查询集群信息
     *
     * @param clusterReqDto
     * @return
     */
    @ApiOperation("根据集群请求参数查询集群信息")
    @PostMapping("/getClusterPage")
    Result<PageData<ClusterRspDto>> getClusterPage(@RequestBody ClusterReqDto clusterReqDto);

    /**
     * 根据集群详情请求参数查询集群详情信息
     *
     * @param clusterDetailReqDto
     * @return
     */
    @ApiOperation("根据集群详情请求参数查询集群详情信息")
    @PostMapping("/getClusterDetail")
    Result<ClusterDetailRspDto> getClusterDetail(@Valid @RequestBody ClusterDetailReqDto clusterDetailReqDto);

    /**
     * 根据集群id查询集群可用资源信息
     *
     * @param clusterId
     * @return
     */
    @ApiOperation("根据集群id查询集群可用资源信息")
    @PostMapping("/{clusterId:\\d+}/available-resource")
    Result<ClusterAppResourceDto> getClusterAvailableResource(@PathVariable("clusterId") Long clusterId);

    /**
     * 根据集群id查询集群marathon地址和监听信息
     *
     * @return
     */
    @ApiOperation("根据集群id查询集群marathon地址和监听信息")
    @PostMapping("/getClusterMarathonConfigs")
    Result<List<ClusterMarathonConfigRspDto>> getClusterMarathonConfigs();

    /**
     * 根据集群id和slaveId查询容器实例日志信息
     *
     * @param clusterDockerInstanceLogReqDto
     * @return
     */
    @ApiOperation("根据集群id和slaveId查询容器实例日志信息")
    @PostMapping("/getClusterDockerInstanceLog")
    Result<ClusterDockerInstanceLogRspDto> getClusterDockerInstanceLog(@Valid @RequestBody ClusterDockerInstanceLogReqDto clusterDockerInstanceLogReqDto);
}
