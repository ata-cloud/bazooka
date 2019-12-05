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
package net.atayun.bazooka.rms.biz.service;

import net.atayun.bazooka.rms.biz.dal.entity.RmsEnvEntity;
import com.youyu.common.service.IService;
import net.atayun.bazooka.rms.api.dto.*;
import net.atayun.bazooka.rms.api.param.*;

import java.util.List;

/**
 * 环境信息接口
 *
 * @author 技术平台
 * @date 2019-07-16
 */
public interface EnvService extends IService<EnvDto, RmsEnvEntity> {

    /**
     * 获取环境所属集群配置信息
     *
     * @param envId
     * @return
     */
    ClusterConfigDto getClusterConfiguration(Long envId);

    /**
     * 获取环境Mlb端口占用情况
     *
     * @param envMlbPortUsedReq
     * @return
     */
    EnvMlbPortUsedRsp getEnvMlbPortUsedInfo(EnvMlbPortUsedReq envMlbPortUsedReq);

    /**
     * 获取环境下剩余可用资源
     *
     * @param envId
     * @return
     */
    EnvResourceDto getEnvAvailableResource(Long envId);

    /**
     * 获取集群下已分配环境资源
     *
     * @param clusterId
     */
    ClusterEnvResourceDto sumClusterUsedResource(Long clusterId);

    /**
     * 获取集群下各环境资源分配明细
     *
     * @param clusterId
     * @return
     */
    List<EnvResourceDto> listClusterEnvUsedResource(Long clusterId);

    /**
     * 根据集群id和appId获取集群应用服务信息
     *
     * @param envAppReq
     * @return
     */
    ClusterAppServiceInfoDto getClusterAppServiceInfo(EnvAppReq envAppReq);

    /**
     * 根据集群id和appId获取集群应用服务地址信息
     *
     * @param envAppReq
     * @return
     */
    List<ClusterAppServiceHostDto> getClusterAppServiceHosts(EnvAppReq envAppReq);

    /**
     * 获取集群App镜像地址
     *
     * @param envAppReq
     * @return
     */
    String getClusterAppImage(EnvAppReq envAppReq);

    /**
     * 根据集群id和appId查询集群容器列表
     *
     * @param envAppReq
     * @return
     */
    List<ClusterDockerDto> getClusterDockers(EnvAppReq envAppReq);

    /**
     * 查询环境列表信息
     *
     * @param envQueryReq
     * @return
     */
    List<EnvDto> list(EnvQueryReq envQueryReq);

    /**
     * 创建环境信息
     *
     * @param envCreateReq
     */
    void create(EnvCreateReq envCreateReq);

    /**
     * 更新环境信息
     *
     * @param envModifyReq
     */
    void update(EnvModifyReq envModifyReq);

    /**
     * 获取环境信息
     *
     * @param envId
     * @return
     */
    EnvDto get(Long envId);

    /**
     * 删除环境信息
     *
     * @param envId
     */
    void delete(Long envId);

    int selectCountByClusterId(Long clusterId);
}




