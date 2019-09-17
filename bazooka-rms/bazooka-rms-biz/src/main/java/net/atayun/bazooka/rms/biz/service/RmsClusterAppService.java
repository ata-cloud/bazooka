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

import net.atayun.bazooka.rms.api.dto.ClusterAppResourceDto;
import net.atayun.bazooka.rms.api.dto.ClusterAppServiceHostDto;
import net.atayun.bazooka.rms.api.dto.ClusterAppServiceInfoDto;
import net.atayun.bazooka.rms.api.dto.RmsClusterAppDto;
import net.atayun.bazooka.rms.biz.dal.entity.RmsClusterAppEntity;
import net.atayun.bazooka.rms.biz.dal.entity.RmsClusterConfigEntity;
import com.youyu.common.service.IService;

import java.util.List;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 资源集群App service
 */
public interface RmsClusterAppService extends IService<RmsClusterAppDto, RmsClusterAppEntity> {

    /**
     * 刷新集群app信息
     *
     * @param rmsClusterConfigEntity
     */
    void refreshClusterAppInfo(RmsClusterConfigEntity rmsClusterConfigEntity);

    /**
     * 获取集群App cpu,内存,磁盘信息
     *
     * @param appId
     * @param clusterId
     * @return
     */
    ClusterAppResourceDto getClusterAppResource(String appId, Long clusterId);

    /**
     * 根据集群id和appId获取集群应用服务信息
     *
     * @param clusterId
     * @param appId
     * @return
     */
    ClusterAppServiceInfoDto getClusterAppServiceInfo(Long clusterId, String appId);

    /**
     * 根据集群id和appId获取集群应用服务地址信息
     *
     * @param clusterId
     * @param appId
     * @return
     */
    List<ClusterAppServiceHostDto> getClusterAppServiceHosts(Long clusterId, String appId);

    /**
     * 获取集群App镜像地址
     *
     * @param clusterId
     * @param appId
     * @return
     */
    String getClusterAppImage(Long clusterId, String appId);
}




