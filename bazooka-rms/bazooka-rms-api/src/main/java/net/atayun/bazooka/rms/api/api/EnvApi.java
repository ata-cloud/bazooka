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

import net.atayun.bazooka.rms.api.dto.*;
import net.atayun.bazooka.rms.api.param.*;
import com.youyu.common.api.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.atayun.bazooka.rms.api.dto.*;
import net.atayun.bazooka.rms.api.param.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: hanxiaorui
 * @Date: 2019/7/18 15:35
 * @Description: 环境资源接口
 */
@Api(value = "环境资源接口", description = "环境资源接口", protocols = "http")
public interface EnvApi {

    /**
     * 获取环境配置
     *
     * @param envId
     * @return
     */
    @ApiOperation(value = "获取环境配置")
    Result<ClusterConfigDto> getEnvConfiguration(@PathVariable("envId") Long envId);

    /**
     * 获取环境MLB端口使用情况
     *
     * @param envMlbPortUsedReq
     * @return
     */
    @ApiOperation(value = "获取环境MLB端口使用情况")
    Result<EnvMlbPortUsedRsp> getEnvMlbPortUsedInfo(@RequestBody @Valid EnvMlbPortUsedReq envMlbPortUsedReq);

    /**
     * 获取环境剩余可用资源
     *
     * @param envId
     * @return
     */
    @ApiOperation(value = "获取环境剩余可用资源")
    Result<EnvResourceDto> getEnvAvailableResource(@PathVariable("envId") Long envId);

    /**
     * 根据集群id和appId获取集群应用服务信息
     *
     * @param envAppReq
     * @return
     */
    @ApiOperation(value = "根据集群id和appId获取集群应用服务信息")
    Result<ClusterAppServiceInfoDto> getClusterAppServiceInfo(@RequestBody EnvAppReq envAppReq);

    /**
     * 根据集群id和appId获取集群应用服务地址信息
     *
     * @param envAppReq
     * @return
     */
    @ApiOperation(value = "根据集群id和appId获取集群应用服务地址信息")
    Result<List<ClusterAppServiceHostDto>> getClusterAppServiceHosts(@RequestBody EnvAppReq envAppReq);

    /**
     * 获取集群App镜像地址
     *
     * @param envAppReq
     * @return
     */
    @ApiOperation(value = "根据集群id和appId获取集群App镜像地址")
    Result<String> getClusterAppImage(@RequestBody @Valid EnvAppReq envAppReq);

    /**
     * 根据集群id和appId查询集群容器列表
     *
     * @param envAppReq
     * @return
     */
    @ApiOperation(value = "根据集群id和appId查询集群容器列表")
    Result<List<ClusterDockerDto>> getClusterDockers(@RequestBody EnvAppReq envAppReq);

    /**
     * 查询环境列表信息
     *
     * @param envQueryReq
     * @return
     */
    @ApiOperation(value = "查询环境列表信息")
    Result<List<EnvDto>> list(@RequestBody EnvQueryReq envQueryReq);

    /**
     * 创建环境信息
     *
     * @param envCreateReq
     */
    @ApiOperation(value = "创建环境信息")
    Result create(@RequestBody EnvCreateReq envCreateReq);

    /**
     * 更新环境信息
     *
     * @param envModifyReq
     */
    @ApiOperation(value = "更新环境信息")
    Result update(@RequestBody EnvModifyReq envModifyReq);

    /**
     * 获取环境信息
     *
     * @param envId
     * @return
     */
    @ApiOperation(value = "获取环境信息")
    Result<EnvDto> get(@PathVariable("envId") Long envId);

    /**
     * 删除环境信息
     *
     * @param envId
     */
    @ApiOperation(value = "删除环境信息")
    Result delete(@PathVariable("envId") Long envId);
}
