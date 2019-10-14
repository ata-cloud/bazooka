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
package net.atayun.bazooka.pms.biz.service.impl;

import net.atayun.bazooka.pms.api.dto.ClusterConfigDto;
import net.atayun.bazooka.pms.api.dto.RmsClusterConfigDto;
import net.atayun.bazooka.pms.biz.dal.dao.RmsClusterConfigMapper;
import net.atayun.bazooka.pms.biz.dal.entity.RmsClusterConfigEntity;
import net.atayun.bazooka.pms.biz.enums.ClusterConfigTypeEnum;
import net.atayun.bazooka.pms.biz.enums.ClusterStatusEnum;
import net.atayun.bazooka.pms.biz.service.RmsClusterConfigService;
import com.youyu.common.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static net.atayun.bazooka.combase.utils.StringUtil.eq;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 资源集群配置 service impl
 */
@Service
public class RmsClusterConfigServiceImpl extends AbstractService<Long, RmsClusterConfigDto, RmsClusterConfigEntity, RmsClusterConfigMapper> implements RmsClusterConfigService {

    @Autowired
    private RmsClusterConfigMapper rmsClusterConfigMapper;

    @Override
    public ClusterConfigDto getClusterConfig(Long clusterId) {
        List<RmsClusterConfigEntity> rmsClusterConfigEntities = rmsClusterConfigMapper.getByClusterId(clusterId);
        return doGetClusterConfig(rmsClusterConfigEntities);
    }

    /**
     * 执行查询集群id查询集群配置信息
     *
     * @param rmsClusterConfigEntities
     * @return
     */
    private ClusterConfigDto doGetClusterConfig(List<RmsClusterConfigEntity> rmsClusterConfigEntities) {
        if (isNull(rmsClusterConfigEntities)) {
            return null;
        }

        ClusterConfigDto clusterConfigDto = new ClusterConfigDto();

        Optional<RmsClusterConfigEntity> marathonOptional = rmsClusterConfigEntities.stream().filter(r -> eq(r.getType(), ClusterConfigTypeEnum.MARATHON.getCode()) && eq(r.getStatus(), ClusterStatusEnum.NORMAL.getCode())).findFirst();
        clusterConfigDto.setDcosEndpoint(marathonOptional.isPresent() ? marathonOptional.get().getUrl() : null);

        List<String> mlbUrls = rmsClusterConfigEntities.stream().filter(r -> eq(r.getType(), ClusterConfigTypeEnum.MLB.getCode())).map(r -> r.getUrl()).collect(toList());
        clusterConfigDto.setMlbUrls(mlbUrls);

        Optional<RmsClusterConfigEntity> dockerHubOptional = rmsClusterConfigEntities.stream().filter(r -> eq(r.getType(), ClusterConfigTypeEnum.DOCKER_HUB.getCode())).findFirst();
        clusterConfigDto.setDockerHubUrl(dockerHubOptional.isPresent() ? dockerHubOptional.get().getUrl() : null);
        return clusterConfigDto;
    }
}




