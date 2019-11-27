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
package net.atayun.bazooka.rms.biz.controller;


import net.atayun.bazooka.rms.api.api.RmsClusterApi;
import net.atayun.bazooka.rms.api.dto.ClusterAppResourceDto;
import net.atayun.bazooka.rms.api.dto.RmsClusterDto;
import net.atayun.bazooka.rms.api.dto.req.ClusterDetailReqDto;
import net.atayun.bazooka.rms.api.dto.req.ClusterDockerInstanceLogReqDto;
import net.atayun.bazooka.rms.api.dto.req.ClusterReqDto;
import net.atayun.bazooka.rms.api.param.CreateClusterReq;
import net.atayun.bazooka.rms.biz.dal.entity.RmsClusterEntity;
import net.atayun.bazooka.rms.biz.service.RmsClusterService;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import io.swagger.annotations.ApiOperation;
import net.atayun.bazooka.rms.api.dto.rsp.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.youyu.common.api.Result.ok;
import static net.atayun.bazooka.base.utils.OrikaCopyUtil.copyProperty;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 资源集群 controller
 */
@RestController
@RequestMapping("/cluster")
public class RmsClusterController implements RmsClusterApi {

    @Autowired
    private RmsClusterService rmsClusterService;

    @Override
    @PostMapping("/getClusterPage")
    public Result<PageData<ClusterRspDto>> getClusterPage(@RequestBody ClusterReqDto clusterReqDto) {
        return ok(rmsClusterService.getClusterPage(clusterReqDto));
    }

    @Override
    @PostMapping("/getClusterDetail")
    public Result<ClusterDetailRspDto> getClusterDetail(@Valid @RequestBody ClusterDetailReqDto clusterDetailReqDto) {
        return ok(rmsClusterService.getClusterDetail(clusterDetailReqDto));
    }

    @Override
    @PostMapping("/{clusterId:\\d+}/available-resource")
    public Result<ClusterAppResourceDto> getClusterAvailableResource(@PathVariable("clusterId") Long clusterId) {
        return ok(rmsClusterService.getClusterAvailableResource(clusterId));
    }

    @Override
    @PostMapping("/getClusterMarathonConfig")
    public Result<List<ClusterMarathonConfigRspDto>> getClusterMarathonConfigs() {
        return ok(rmsClusterService.getClusterMarathonConfigs());
    }

    @Override
    @PostMapping("/getClusterDockerInstanceLog")
    public Result<ClusterDockerInstanceLogRspDto> getClusterDockerInstanceLog(@Valid @RequestBody ClusterDockerInstanceLogReqDto clusterDockerInstanceLogReqDto) {
        return ok(rmsClusterService.getClusterDockerInstanceLog(clusterDockerInstanceLogReqDto));
    }

    @ApiOperation("获取集群组件信息")
    @GetMapping("components/{clusterName}")
    public Result<ClusterComponentsDto> getClusterComponentsInfo(@PathVariable String clusterName) {
        ClusterComponentsDto clusterComponents = this.rmsClusterService.getClusterComponentsInfo(clusterName);
        return Result.ok(clusterComponents);
    }

    @ApiOperation("新建单节点集群")
    @PostMapping("/createSingleNodeCluster")
    public Result createSingleNodeCluster(@RequestBody @Valid CreateClusterReq createClusterReq){
        return rmsClusterService.createSingleNodeCluster(createClusterReq);
    }

    @ApiOperation("新建mesos集群")
    @PostMapping("/createMesosCluster")
    public Result createMesosCluster(@RequestBody @Valid CreateClusterReq createClusterReq){
        return rmsClusterService.createMesosCluster(createClusterReq);
    }

    @ApiOperation("根据id获取集群基本信息")
    @PostMapping("/getClusterInfo")
    public Result<RmsClusterDto> getClusterInfo(@RequestParam("id") Long id) {
        RmsClusterEntity rmsClusterEntity = rmsClusterService.getClusterInfo(id);
        RmsClusterDto rmsClusterDto = copyProperty(rmsClusterEntity,RmsClusterDto.class);
        return Result.ok(rmsClusterDto);
    }
}
