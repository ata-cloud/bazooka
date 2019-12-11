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
package net.atayun.bazooka.rms.api.api;

import io.swagger.annotations.Api;
import net.atayun.bazooka.rms.api.dto.ClusterAppResourceDto;
import net.atayun.bazooka.rms.api.dto.RmsClusterDto;
import net.atayun.bazooka.rms.api.dto.req.ClusterDetailReqDto;
import net.atayun.bazooka.rms.api.dto.req.ClusterDockerInstanceLogReqDto;
import net.atayun.bazooka.rms.api.dto.req.ClusterReqDto;
import net.atayun.bazooka.rms.api.dto.rsp.ClusterDetailRspDto;
import net.atayun.bazooka.rms.api.dto.rsp.ClusterDockerInstanceLogRspDto;
import net.atayun.bazooka.rms.api.dto.rsp.ClusterMarathonConfigRspDto;
import net.atayun.bazooka.rms.api.dto.rsp.ClusterRspDto;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import io.swagger.annotations.ApiOperation;
import net.atayun.bazooka.rms.api.param.CreateClusterReq;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 资源集群 Api
 */
@Api(value = "集群资源接口", description = "集群资源接口", protocols = "http")
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

    /**
     * @create: zhangyingbin 2019/11/8 0008 下午 2:27
     * @Modifier:
     * @Description: 创建单节点集群
     */
    @ApiOperation("新建单节点集群")
    @PostMapping("/createSingleNodeCluster")
    Result createSingleNodeCluster(@Valid @RequestBody CreateClusterReq createClusterReq);

    /**
     * @create: zhangyingbin 2019/11/8 0008 下午 5:16
     * @Modifier:
     * @Description: 创建mesos集群
     */
    @ApiOperation("新建mesos集群")
    @PostMapping("/createMesosCluster")
    Result createMesosCluster(@Valid @RequestBody CreateClusterReq createClusterReq);

    /**
     * @create: zhangyingbin 2019/11/11 0011 下午 5:32
     * @Modifier:
     * @Description: 根据id获取集群基本信息
     */
    @ApiOperation("根据id获取集群基本信息")
    @PostMapping("/getClusterInfo")
    Result<RmsClusterDto> getClusterInfo(@RequestParam("id")  Long id);
}
