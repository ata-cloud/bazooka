package net.atayun.bazooka.rms.biz.service;

import net.atayun.bazooka.base.enums.AppOptEnum;
import net.atayun.bazooka.rms.api.dto.EnvResourceDto;
import net.atayun.bazooka.rms.api.enums.ClusterAppServiceStatusEnum;
import net.atayun.bazooka.rms.biz.dal.entity.ContainerAndResourceSumEntity;
import net.atayun.bazooka.rms.biz.dal.entity.RmsContainer;
import net.atayun.bazooka.rms.api.dto.NodeAvailableResourceDto;
import net.atayun.bazooka.rms.api.dto.ResourceSumDto;
import net.atayun.bazooka.rms.api.param.NodeContainerParam;

import java.util.List;

/**
 * @author Ping
 */
public interface RmsContainerService {

    ResourceSumDto sumResourceByClusterId(Long clusterId);

    int sumContainerByClusterId(Long clusterId);

    List<EnvResourceDto> sumResourceByClusterIdGroupByEnv(Long clusterId);

    ContainerAndResourceSumEntity sumContainerAndResourceByNode(Long nodeId);

    List<RmsContainer> selectByAppId(Long appId);

    List<RmsContainer> selectByEnvIdAndAppIdAndStatus(Long envId, Long appId, String status);

    NodeAvailableResourceDto getNodeAvailableResource(List<Long> nodeIds);

    void insert(Long envId, Long appId, AppOptEnum opt, List<NodeContainerParam> nodeContainerParam);

    List<RmsContainer> selectByEnvIdAndAppId(Long envId, Long appIdLongValue);
}
