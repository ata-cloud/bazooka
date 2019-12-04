package net.atayun.bazooka.deploy.biz.v2.service.app.step.deploymode;

import com.youyu.common.exception.BizException;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.log.StepLogBuilder;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.feign.AppApi;
import net.atayun.bazooka.rms.api.api.RmsClusterNodeApi;
import net.atayun.bazooka.rms.api.api.RmsContainerApi;
import net.atayun.bazooka.rms.api.dto.NodeAvailableResourceDto;
import net.atayun.bazooka.rms.api.dto.ResourceSumDto;
import net.atayun.bazooka.rms.api.dto.rsp.ClusterNodeRspDto;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;
import static net.atayun.bazooka.deploy.biz.v2.constant.DeployResultCodeConstants.*;

/**
 * @author Ping
 */
public interface ICheckNodeResource {

    default void checkNodeResource(AppOpt appOpt, StepLogBuilder stepLogBuilder) {
        stepLogBuilder.append("检查资源");
        AppDeployConfigDto deployConfig = getBean(AppApi.class).getAppDeployConfigInfoById(appOpt.getDeployConfigId())
                .ifNotSuccessThrowException().getData();
        List<Long> clusterNodeIds = deployConfig.getClusterNodes();
        NodeAvailableResourceDto nodeAvailableResourceDto = getBean(RmsContainerApi.class).getEnvAvailableResource(clusterNodeIds)
                .ifNotSuccessThrowException().getData();
        Map<Long, ResourceSumDto> availableResource = nodeAvailableResourceDto.getAvailableResource();
        if (CollectionUtils.isEmpty(availableResource)) {
            throw new BizException(RESOURCE_ERR_CODE, "未配置节点");
        }

        List<ClusterNodeRspDto> clusterNodes = getBean(RmsClusterNodeApi.class).getClusterNodeInfoByNodeIds(clusterNodeIds)
                .ifNotSuccessThrowException().getData();
        for (ClusterNodeRspDto clusterNode : clusterNodes) {
            Long nodeId = clusterNode.getId();
            ResourceSumDto available = availableResource.get(nodeId);
            if (available == null) {
                throw new BizException(RESOURCE_ERR_CODE, "节点[" + clusterNode.getIp() + "]无可用资源");
            }
            if (Optional.ofNullable(available.getCpu()).orElse(BigDecimal.ZERO).subtract(new BigDecimal(deployConfig.getCpus())).compareTo(BigDecimal.ZERO) < 0) {
                throw new BizException(ILLEGAL_CPU_SHARES, "节点[" + clusterNode.getIp() + "]CPU核数不足");
            }
            if (Optional.ofNullable(available.getMemory()).orElse(BigDecimal.ZERO).subtract(new BigDecimal(deployConfig.getMemory())).compareTo(BigDecimal.ZERO) < 0) {
                throw new BizException(ILLEGAL_MEM, "节点[" + clusterNode.getIp() + "]内存核数不足");
            }
//            if (Optional.ofNullable(available.getDisk()).orElse(BigDecimal.ZERO).subtract(new BigDecimal(deployConfig.getDisk())).compareTo(BigDecimal.ZERO) < 0) {
//                throw new BizException(ILLEGAL_DISK, "节点[" + clusterNode.getIp() + "]磁盘核数不足");
//            }
        }
    }

    default void checkNodeResource(List<Long> nodeIds, BigDecimal cpu, BigDecimal mem, StepLogBuilder stepLogBuilder) {
        stepLogBuilder.append("检查资源");
        NodeAvailableResourceDto nodeAvailableResourceDto = getBean(RmsContainerApi.class).getEnvAvailableResource(nodeIds)
                .ifNotSuccessThrowException().getData();
        Map<Long, ResourceSumDto> availableResource = nodeAvailableResourceDto.getAvailableResource();
        if (CollectionUtils.isEmpty(availableResource)) {
            throw new BizException(RESOURCE_ERR_CODE, "未配置节点");
        }

        List<ClusterNodeRspDto> clusterNodes = getBean(RmsClusterNodeApi.class).getClusterNodeInfoByNodeIds(nodeIds)
                .ifNotSuccessThrowException().getData();
        for (ClusterNodeRspDto clusterNode : clusterNodes) {
            Long nodeId = clusterNode.getId();
            ResourceSumDto available = availableResource.get(nodeId);
            if (available == null) {
                throw new BizException(RESOURCE_ERR_CODE, "节点[" + clusterNode.getIp() + "]无可用资源");
            }
            if (Optional.ofNullable(available.getCpu()).orElse(BigDecimal.ZERO).subtract(cpu).compareTo(BigDecimal.ZERO) < 0) {
                throw new BizException(ILLEGAL_CPU_SHARES, "节点[" + clusterNode.getIp() + "]CPU核数不足");
            }
            if (Optional.ofNullable(available.getMemory()).orElse(BigDecimal.ZERO).subtract(mem).compareTo(BigDecimal.ZERO) < 0) {
                throw new BizException(ILLEGAL_MEM, "节点[" + clusterNode.getIp() + "]内存核数不足");
            }
        }
    }
}
