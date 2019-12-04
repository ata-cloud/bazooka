package net.atayun.bazooka.rms.biz.service.impl;

import com.youyu.common.utils.YyBeanUtils;
import net.atayun.bazooka.base.enums.AppOptEnum;
import net.atayun.bazooka.base.service.BatchService;
import net.atayun.bazooka.rms.api.dto.EnvResourceDto;
import net.atayun.bazooka.rms.api.dto.NodeAvailableResourceDto;
import net.atayun.bazooka.rms.api.dto.ResourceSumDto;
import net.atayun.bazooka.rms.api.enums.ClusterAppServiceStatusEnum;
import net.atayun.bazooka.rms.api.param.NodeContainerParam;
import net.atayun.bazooka.rms.biz.dal.dao.RmsClusterNodeMapper;
import net.atayun.bazooka.rms.biz.dal.dao.RmsContainerMapper;
import net.atayun.bazooka.rms.biz.dal.entity.*;
import net.atayun.bazooka.rms.biz.service.RmsContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Ping
 */
@Service
public class RmsContainerServiceImpl implements RmsContainerService {

    @Autowired
    private RmsContainerMapper rmsContainerMapper;

    @Autowired
    private BatchService batchService;

    @Autowired
    private RmsClusterNodeMapper rmsClusterNodeMapper;

    @Override
    public ResourceSumDto sumResourceByClusterId(Long clusterId) {
        ResourceSumEntity resourceSumEntity = rmsContainerMapper.selectResourceByClusterId(clusterId);
        ResourceSumDto resourceSumDto = new ResourceSumDto();
        if (resourceSumEntity == null) {
            return resourceSumDto;
        }
        YyBeanUtils.copyProperties(resourceSumEntity, resourceSumDto);
        return resourceSumDto;
    }

    @Override
    public int sumContainerByClusterId(Long clusterId) {
        RmsContainer rmsContainer = new RmsContainer();
        rmsContainer.setClusterId(clusterId);
        return rmsContainerMapper.selectCount(rmsContainer);
    }

    @Override
    public List<EnvResourceDto> sumResourceByClusterIdGroupByEnv(Long clusterId) {
        List<ResourceSumEnvGroupEntity> resourceSumEnvGroupEntities = rmsContainerMapper.sumResourceByClusterIdGroupByEnv(clusterId);
        if (CollectionUtils.isEmpty(resourceSumEnvGroupEntities)) {
            return new ArrayList<>();
        }
        return resourceSumEnvGroupEntities.stream()
                .map(resourceSumEnvGroupEntity -> {
                    EnvResourceDto resourceSumEnvGroupDto = new EnvResourceDto();
                    YyBeanUtils.copyProperties(resourceSumEnvGroupEntity, resourceSumEnvGroupDto);
                    return resourceSumEnvGroupDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ContainerAndResourceSumEntity sumContainerAndResourceByNode(String nodeId) {
        ContainerAndResourceSumEntity containerAndResourceSumEntity = rmsContainerMapper.sumContainerAndResourceByNode(nodeId);
        return containerAndResourceSumEntity == null ? new ContainerAndResourceSumEntity() : containerAndResourceSumEntity;
    }

    @Override
    public List<RmsContainer> selectByAppId(Long appId) {
        Example example = new Example(RmsContainer.class);
        example.createCriteria().andEqualTo("appId", appId);
        List<RmsContainer> rmsContainers = rmsContainerMapper.selectByExample(example);
        return CollectionUtils.isEmpty(rmsContainers) ? new ArrayList<>() : rmsContainers;
    }

    @Override
    public List<RmsContainer> selectByAppIdAndStatus(Long appId, ClusterAppServiceStatusEnum status) {
        Example example = new Example(RmsContainer.class);
        example.createCriteria().andEqualTo("appId", appId)
                .andEqualTo("containerStatus", status);
        List<RmsContainer> rmsContainers = rmsContainerMapper.selectByExample(example);
        return CollectionUtils.isEmpty(rmsContainers) ? new ArrayList<>() : rmsContainers;
    }

    @Override
    public NodeAvailableResourceDto getNodeAvailableResource(List<Long> nodeIds) {
        Map<Long, ResourceSumDto> map = new HashMap<>();
        for (Long nodeId : nodeIds) {
            RmsClusterNodeEntity rmsClusterNodeEntity = rmsClusterNodeMapper.selectByPrimaryKey(nodeId);
            List<RmsContainer> rmsContainers = selectByNodeId(nodeId);
            ResourceSumDto resourceSumDto = new ResourceSumDto();
            if (CollectionUtils.isEmpty(rmsContainers)) {
                resourceSumDto.setCpu(rmsClusterNodeEntity.getCpu());
                resourceSumDto.setMemory(rmsClusterNodeEntity.getMemory());
                resourceSumDto.setDisk(rmsClusterNodeEntity.getDisk());
            } else {
                double cpu = rmsContainers.stream().mapToDouble(rmsContainer -> rmsContainer.getCpu().doubleValue()).sum();
                double mem = rmsContainers.stream().mapToDouble(rmsContainer -> rmsContainer.getMemory().doubleValue()).sum();
                double disk = rmsContainers.stream().mapToDouble(rmsContainer -> rmsContainer.getDisk().doubleValue()).sum();
                resourceSumDto.setCpu(
                        Optional.ofNullable(rmsClusterNodeEntity.getCpu()).orElse(BigDecimal.ZERO)
                                .subtract(new BigDecimal(cpu))
                                .max(BigDecimal.ZERO));
                resourceSumDto.setMemory(
                        Optional.ofNullable(rmsClusterNodeEntity.getMemory()).orElse(BigDecimal.ZERO)
                                .subtract(new BigDecimal(mem))
                                .max(BigDecimal.ZERO));
                resourceSumDto.setDisk(
                        Optional.ofNullable(rmsClusterNodeEntity.getDisk()).orElse(BigDecimal.ZERO)
                                .subtract(new BigDecimal(disk))
                                .max(BigDecimal.ZERO));
            }
            map.put(nodeId, resourceSumDto);
        }
        return new NodeAvailableResourceDto(map);
    }

    private List<RmsContainer> selectByNodeId(Long nodeId) {
        Example example = new Example(RmsContainer.class);
        example.createCriteria().andEqualTo("nodeId", nodeId);
        return rmsContainerMapper.selectByExample(example);
    }

    private void updateStatusByAppId(Long appId, ClusterAppServiceStatusEnum status) {
        rmsContainerMapper.updateStatusByAppId(appId, status.getCode());
    }

    @Override
    public void insert(Long appId, AppOptEnum opt, List<NodeContainerParam> nodeContainerParams) {
        updateStatusByAppId(appId, ClusterAppServiceStatusEnum.CLOSED);
        if (opt == AppOptEnum.STOP) {
            return;
        }
        if (CollectionUtils.isEmpty(nodeContainerParams)) {
            return;
        }
        List<RmsContainer> collect = nodeContainerParams.stream().map(nodeContainerParam -> {
            RmsContainer rmsContainer = new RmsContainer();
            YyBeanUtils.copyProperties(nodeContainerParam, rmsContainer);
            return rmsContainer;
        }).collect(Collectors.toList());

        batchService.batchDispose(collect, RmsContainerMapper.class, "insertSelective");
    }
}
