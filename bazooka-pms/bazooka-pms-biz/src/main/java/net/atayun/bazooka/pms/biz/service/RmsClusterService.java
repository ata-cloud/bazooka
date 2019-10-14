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
package net.atayun.bazooka.pms.biz.service;

import net.atayun.bazooka.combase.dcos.dto.MesosTaskDto;
import net.atayun.bazooka.combase.dcos.dto.TaskDto;
import net.atayun.bazooka.pms.api.dto.ClusterAppResourceDto;
import net.atayun.bazooka.pms.api.dto.ClusterDockerDto;
import net.atayun.bazooka.pms.api.dto.RmsClusterDto;
import net.atayun.bazooka.pms.api.dto.req.ClusterDetailReqDto;
import net.atayun.bazooka.pms.api.dto.req.ClusterDockerInstanceLogReqDto;
import net.atayun.bazooka.pms.api.dto.req.ClusterReqDto;
import net.atayun.bazooka.pms.biz.dal.entity.RmsClusterEntity;
import com.youyu.common.api.PageData;
import com.youyu.common.service.IService;
import net.atayun.bazooka.pms.api.dto.rsp.*;

import java.util.List;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 资源集群 service
 */
public interface RmsClusterService extends IService<RmsClusterDto, RmsClusterEntity> {

    /**
     * 刷新集群信息
     *
     * @param rmsClusterService
     */
    void refreshClusterInfo(RmsClusterService rmsClusterService);

    /**
     * 刷新集群相关信息
     *
     * @param rmsClusterEntity
     */
    void refreshClusterInfo(RmsClusterEntity rmsClusterEntity);

    /**
     * 获取集群容器task信息
     *
     * @param clusterId
     * @return
     */
    TaskDto getTask(Long clusterId);

    /**
     * 获取集群容器Mesos task信息
     *
     * @param clusterId
     * @return
     */
    MesosTaskDto getMesosTask(Long clusterId);

    /**
     * 根据集群请求参数查询集群信息
     *
     * @param clusterReqDto
     * @return
     */
    PageData<ClusterRspDto> getClusterPage(ClusterReqDto clusterReqDto);

    /**
     * 查询集群可用资源
     *
     * @param clusterId
     * @return
     */
    ClusterAppResourceDto getClusterAvailableResource(Long clusterId);

    /**
     * 根据集群id查询集群可用资源信息
     *
     * @param clusterDetailReqDto
     * @return
     */
    ClusterDetailRspDto getClusterDetail(ClusterDetailReqDto clusterDetailReqDto);

    /**
     * 根据集群id和appId查询集群容器列表
     *
     * @param clusterId
     * @param appId
     * @return
     */
    List<ClusterDockerDto> getClusterDockers(Long clusterId, String appId);

    /**
     * 根据集群id查询集群marathon地址和监听信息
     *
     * @return
     */
    List<ClusterMarathonConfigRspDto> getClusterMarathonConfigs();

    /**
     * 根据集群id和slaveId查询容器实例日志信息
     *
     * @param clusterDockerInstanceLogReqDto
     * @return
     */
    ClusterDockerInstanceLogRspDto getClusterDockerInstanceLog(ClusterDockerInstanceLogReqDto clusterDockerInstanceLogReqDto);

    /**
     * 获取集群信息
     *
     * @param clusterName
     * @return
     */
    ClusterComponentsDto getClusterComponentsInfo(String clusterName);
}




