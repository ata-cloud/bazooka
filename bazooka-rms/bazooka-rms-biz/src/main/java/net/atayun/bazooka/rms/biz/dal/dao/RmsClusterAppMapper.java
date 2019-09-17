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
package net.atayun.bazooka.rms.biz.dal.dao;

import net.atayun.bazooka.rms.api.dto.ClusterAppResourceDto;
import net.atayun.bazooka.rms.biz.dal.entity.RmsClusterAppEntity;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 资源集群App mapper
 */
public interface RmsClusterAppMapper extends YyMapper<RmsClusterAppEntity> {

    /**
     * 更新appId的列表为未激活
     *
     * @param appId
     */
    void updateInactiveByAppId(String appId);

    /**
     * 根据集群id和appId和active获取集群资源
     *
     * @param clusterId
     * @param appId
     * @param active
     * @return
     */
    ClusterAppResourceDto getClusterAppResourceByClusterIdAppIdActive(@Param("clusterId") Long clusterId, @Param("appId") String appId, @Param("active") boolean active);

    /**
     * 查询运行中和所有的服务数量
     *
     * @param clusterId
     * @return
     */
    List<Integer> getRunningAndAllClusterAppQuantity(Long clusterId);

    /**
     * 根据集群id和appId和active获取集群app信息
     *
     * @param clusterId
     * @param appId
     * @param active
     * @return
     */
    RmsClusterAppEntity getClusterAppByClusterIdAppIdActive(@Param("clusterId") Long clusterId, @Param("appId") String appId, @Param("active") boolean active);

    /**
     * 更新全部为未激活
     * @param clusterId
     */
    void updateAllInactiveByClusterId(Long clusterId);
}




