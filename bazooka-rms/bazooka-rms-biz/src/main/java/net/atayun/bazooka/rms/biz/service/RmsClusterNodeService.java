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

import net.atayun.bazooka.rms.api.dto.RmsClusterNodeDto;
import net.atayun.bazooka.rms.api.dto.req.ClusterNodeReqDto;
import net.atayun.bazooka.rms.api.dto.rsp.ClusterNodeRspDto;
import net.atayun.bazooka.rms.biz.dal.entity.RmsClusterConfigEntity;
import net.atayun.bazooka.rms.biz.dal.entity.RmsClusterNodeEntity;
import com.youyu.common.api.PageData;
import com.youyu.common.service.IService;

import java.util.List;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 资源集群节点 service
 */
public interface RmsClusterNodeService extends IService<RmsClusterNodeDto, RmsClusterNodeEntity> {

    /**
     * 刷新集群节点信息,返回节点信息列表
     *
     * @param rmsClusterConfigEntity
     * @return
     */
    List<RmsClusterNodeEntity> refreshClusterNodeInfo(RmsClusterConfigEntity rmsClusterConfigEntity);

    /**
     * 查询集群节点列表信息
     *
     * @param clusterNodeReqDto
     * @return
     */
    PageData<ClusterNodeRspDto> getClusterNodePage(ClusterNodeReqDto clusterNodeReqDto);
}




