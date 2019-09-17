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

import net.atayun.bazooka.pms.api.dto.AppInfoDto;
import net.atayun.bazooka.pms.api.dto.PmsServicePortDistributionDto;
import net.atayun.bazooka.pms.api.enums.ServicePortState;
import net.atayun.bazooka.pms.biz.dal.dao.PmsServicePortDistributionMapper;
import net.atayun.bazooka.pms.biz.dal.entity.PmsProjectEnvRelationEntity;
import net.atayun.bazooka.pms.biz.dal.entity.PmsServicePortDistributionEntity;
import net.atayun.bazooka.pms.biz.service.AppInfoService;
import net.atayun.bazooka.pms.biz.service.PmsProjectEnvRelationService;
import net.atayun.bazooka.pms.biz.service.PmsServicePortDistributionService;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import com.youyu.common.service.AbstractService;
import com.youyu.common.utils.YyAssert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2019-07-31
 */
@Slf4j
@Service
public class PmsServicePortDistributionServiceImpl extends AbstractService<Long, PmsServicePortDistributionDto, PmsServicePortDistributionEntity, PmsServicePortDistributionMapper> implements PmsServicePortDistributionService {
    @Autowired
    private PmsProjectEnvRelationService projectEnvRelationService;
    @Autowired
    private AppInfoService appInfoService;

    /**
     * 分配端口
     * <p>
     * 项目环境下没有分配过端口->从头开始分配
     *
     * @param projectId
     * @param appId
     * @param envId
     * @param containerPort
     * @return
     */
    @Override
    public Integer distributionServicePort(Long projectId, Long appId, Long envId, Integer containerPort) {
        log.info("distributionServicePort:[appId: {}, envId: {}, containerPort:{}]", appId, envId, containerPort);
        PmsProjectEnvRelationEntity projectEnvRelationEntity = this.projectEnvRelationService.selectOneEntity(new PmsProjectEnvRelationEntity(projectId, envId));
        log.info("项目环境下的端口范围：[{} - {}]", projectEnvRelationEntity.getPortStart(), projectEnvRelationEntity.getPortEnd());

        log.info("查询项目环境下已分配过的端口");
        List<PmsServicePortDistributionEntity> servicePortDistributionList = this.selectEntity(new PmsServicePortDistributionEntity(projectId, envId));

        Integer servicePort;

        if (ObjectUtils.isEmpty(servicePortDistributionList)) {
            log.info("从头开始分配");
            servicePort = projectEnvRelationEntity.getPortStart();
        } else {
            log.info("检查应用环境下容器端口是否分配过服务端口");
            Optional<Integer> servicePortOptional = servicePortDistributionList.stream()
                    .filter(dto -> appId.equals(dto.getAppId()) && containerPort.equals(dto.getContainerPort()))
                    .map(dto -> dto.getServicePort())
                    .findFirst();
            if (servicePortOptional.isPresent()) {
                log.info("返回已分配过的端口");
                return servicePortOptional.get();
            } else {
                log.info("检查是否有回收的端口");
                Optional<PmsServicePortDistributionEntity> recycledPort = servicePortDistributionList.stream()
                        .filter(
                                port -> ServicePortState.Recycled.equals(port.getPortState())
                                        || (ServicePortState.Created.equals(port.getPortState()) && LocalDateTime.now().isAfter(port.getUpdateTime().plusDays(1L)))
                        )
                        .findFirst();
                if (recycledPort.isPresent()) {
                    log.info("返回回收的端口：[{}]", recycledPort.get().getServicePort());
                    PmsServicePortDistributionEntity portDistributionEntity = recycledPort.get();
                    portDistributionEntity.setAppId(appId);
                    portDistributionEntity.setContainerPort(containerPort);
                    portDistributionEntity.setPortState(ServicePortState.Created);
                    this.updateByPrimaryKeySelective(portDistributionEntity);
                    return portDistributionEntity.getServicePort();
                }

                log.info("按照顺序分配新的服务端口");
                Optional<PmsServicePortDistributionEntity> portDistributionEntityOptional = servicePortDistributionList.stream()
                        .filter(port -> port.getContinuous())
                        .sorted(Comparator.comparing(PmsServicePortDistributionEntity::getServicePort).reversed())
                        .findFirst();
                List<Integer> projectPortDistribution = servicePortDistributionList.stream().map(PmsServicePortDistributionEntity::getServicePort).collect(Collectors.toList());
                if (portDistributionEntityOptional.isPresent()) {
                    servicePort = this.getNewServicePort(portDistributionEntityOptional.get().getServicePort(), projectPortDistribution);
                } else {
                    //从头开始分配
                    servicePort = this.getNewServicePort(projectEnvRelationEntity.getPortStart(), projectPortDistribution);
                }
            }
        }

        if (servicePort > projectEnvRelationEntity.getPortEnd()) {
            log.warn("项目端口不足，无法分配新的端口！");
            return null;
        } else {
            this.insertSelective(new PmsServicePortDistributionEntity(projectId, appId, envId, containerPort, servicePort, Boolean.TRUE, ServicePortState.Created));
            return servicePort;
        }
    }

    /**
     * 按照顺序分配一个新的没有被占用的端口
     *
     * @param currentMaxPort
     * @param projectPortDistribution
     * @return
     */
    private Integer getNewServicePort(Integer currentMaxPort, List<Integer> projectPortDistribution) {
        Integer servicePort = ++currentMaxPort;
        if (projectPortDistribution.contains(servicePort)) {
            return getNewServicePort(servicePort, projectPortDistribution);
        } else {
            return servicePort;
        }
    }

    /**
     * 服务端口检查（是否可用）
     *
     * @param projectId
     * @param appId
     * @param envId
     * @param containerPort
     * @param servicePort
     */
    @Override
    public void servicePortCheck(Long projectId, Long appId, Long envId, Integer containerPort, Integer servicePort) {
        //查询项目环境下端口的分配信息
        PmsProjectEnvRelationEntity projectEnvRelationEntity = this.projectEnvRelationService.selectOneEntity(new PmsProjectEnvRelationEntity(projectId, envId));
        int startPort = projectEnvRelationEntity.getPortStart();
        int endPort = projectEnvRelationEntity.getPortEnd();
        YyAssert.paramCheck(servicePort < startPort || servicePort > endPort, MessageFormat.format("LB分配端口{0,number,#}不在[{1,number,#}~{2,number,#}]范围内", servicePort, startPort, endPort));

        List<PmsServicePortDistributionDto> portDistributionDtoList = this.select(new PmsServicePortDistributionEntity(projectId, envId, servicePort));
        if (ObjectUtils.isEmpty(portDistributionDtoList)) {
            //没有分配
            return;
        } else {
            PmsServicePortDistributionDto portDistributionDto = portDistributionDtoList.get(0);
            if (appId.equals(portDistributionDto.getAppId())) {
                //分给当前应用了
                if (portDistributionDto.getContainerPort().equals(containerPort)) {
                    return;
                } else {
                    throw new BizException(BaseResultCode.REQUEST_PARAMS_TYPE_WRONG.getCode(), MessageFormat.format("当前环境下的[{0,number,#}]端口已分配给应用的[{1,number,#}]端口，与当前端口[{2,number,#}]不符。", servicePort, portDistributionDto.getContainerPort(), containerPort));
                }
            } else {
                AppInfoDto appInfoDto = this.appInfoService.selectByPrimaryKey(portDistributionDto.getAppId());
                throw new BizException(BaseResultCode.REQUEST_PARAMS_TYPE_WRONG.getCode(), MessageFormat.format("当前环境下的[{0,number,#}]端口已分配给应用[{1}]", servicePort, appInfoDto.getAppName()));
            }
        }
    }

    /**
     * 保存分配端口信息
     *
     * @param projectId
     * @param appId
     * @param envId
     * @param configId
     * @param containerPort
     * @param servicePort
     * @return
     */
    @Override
    public int saveServicePort(Long projectId, Long appId, Long envId, Long configId, Integer containerPort, Integer servicePort) {
        this.servicePortCheck(projectId, appId, envId, containerPort, servicePort);

        PmsServicePortDistributionEntity portDistributionEntity = this.selectOneEntity(new PmsServicePortDistributionEntity(projectId, appId, envId, containerPort, servicePort, null, null));
        if (ObjectUtils.isEmpty(portDistributionEntity)) {
            //不是自动分配的，用户自己填的
            PmsServicePortDistributionEntity userDefinedServicePortEntity = new PmsServicePortDistributionEntity(projectId, appId, envId, containerPort, servicePort, Boolean.FALSE, ServicePortState.Saved);
            userDefinedServicePortEntity.setConfigId(configId);
            return this.insertSelective(userDefinedServicePortEntity);
        } else if (ServicePortState.Created.equals(portDistributionEntity.getPortState())) {
            //已分配，直接更新状态为保存
            portDistributionEntity.setPortState(ServicePortState.Saved);
            portDistributionEntity.setConfigId(configId);
            return this.updateByPrimaryKeySelective(portDistributionEntity);
        }
        return 0;
    }
}




