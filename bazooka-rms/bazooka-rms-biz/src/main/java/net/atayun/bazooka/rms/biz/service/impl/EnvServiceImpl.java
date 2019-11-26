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
package net.atayun.bazooka.rms.biz.service.impl;

import net.atayun.bazooka.base.mlb.MlbServiceBean;
import net.atayun.bazooka.base.mlb.MlbServiceDto;
import net.atayun.bazooka.rms.biz.dal.dao.RmsEnvLogMapper;
import net.atayun.bazooka.rms.biz.dal.dao.RmsEnvMapper;
import net.atayun.bazooka.rms.biz.dal.entity.RmsEnvEntity;
import net.atayun.bazooka.rms.biz.dal.entity.RmsEnvLogEntity;
import net.atayun.bazooka.rms.biz.enums.ClusterStatusEnum;
import net.atayun.bazooka.rms.biz.enums.OptType;
import net.atayun.bazooka.rms.biz.service.EnvService;
import net.atayun.bazooka.rms.biz.service.RmsClusterAppService;
import net.atayun.bazooka.rms.biz.service.RmsClusterConfigService;
import net.atayun.bazooka.rms.biz.service.RmsClusterService;
import com.youyu.common.service.AbstractService;
import net.atayun.bazooka.rms.api.dto.*;
import net.atayun.bazooka.rms.api.param.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static net.atayun.bazooka.base.utils.CommonUtil.defaultValue;
import static net.atayun.bazooka.base.utils.OrikaCopyUtil.copyProperty;
import static net.atayun.bazooka.rms.api.enums.EnvState.NORMAL;
import static com.google.common.collect.Lists.newArrayList;
import static com.youyu.common.utils.YyAssert.assertTrue;
import static java.math.BigDecimal.ZERO;
import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static net.atayun.bazooka.rms.api.enums.RmsResultCode.*;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;


/**
 * 环境信息实现
 *
 * @author 技术平台
 * @date 2019-07-16
 */
@Service
public class EnvServiceImpl extends AbstractService<Long, EnvDto, RmsEnvEntity, RmsEnvMapper> implements EnvService {

    @Autowired
    private RmsClusterService clusterService;

    @Autowired
    private RmsClusterConfigService clusterConfigService;

    @Autowired
    private RmsClusterAppService clusterAppService;

    @Autowired
    private MlbServiceBean mlbServiceBean;

    @Autowired
    private RmsEnvLogMapper envLogMapper;

    private static final String APP_ID_FORMAT = "/{0}/{1}";

    @Override
    public ClusterConfigDto getClusterConfiguration(Long envId) {

        RmsEnvEntity record = this.selectEntityByPrimaryKey(envId);
        assertTrue(isNull(record), ENV_NOT_EXISTS);
        // get config from cluster service
        return clusterConfigService.getClusterConfig(record.getClusterId());
    }

    @Override
    public EnvMlbPortUsedRsp getEnvMlbPortUsedInfo(EnvMlbPortUsedReq envMlbPortUsedReq) {

        ClusterConfigDto clusterConfiguration = getClusterConfiguration(envMlbPortUsedReq.getEnvId());
        List<String> mlbUrls = clusterConfiguration.getMlbUrls();
        assertTrue(isEmpty(mlbUrls), CLUSTER_RESOURCE_UNAVAILABLE);

        EnvMlbPortUsedRsp envMlbPortUsedRsp = new EnvMlbPortUsedRsp();
        envMlbPortUsedRsp.setUsed(false);
        String mlbUrl = mlbUrls.get(0); // for current version
        List<MlbServiceDto> mlbServices = mlbServiceBean.getMlbServices(mlbUrl);
        if (isEmpty(mlbServices)) {
            return envMlbPortUsedRsp;
        }
        Optional<MlbServiceDto> mlbServiceOptional = mlbServices
                .stream()
                .filter(mlbService -> envMlbPortUsedReq.getPort() == mlbService.getPort())
                .findAny();
        if (!mlbServiceOptional.isPresent()) {
            return envMlbPortUsedRsp;
        }
        envMlbPortUsedRsp.setUsed(true);
        envMlbPortUsedRsp.setService(mlbServiceOptional.get().getService());
        return envMlbPortUsedRsp;
    }

    @Override
    public EnvResourceDto getEnvAvailableResource(Long envId) {

        RmsEnvEntity record = this.selectEntityByPrimaryKey(envId);
        assertTrue(isNull(record), ENV_NOT_EXISTS);
        EnvResourceDto envAvailableResource = convertToEnvResource(record);
        // get env used resource
        Optional<ClusterAppResourceDto> envUsedOptional = ofNullable(clusterAppService.getClusterAppResource(record.getCode(), record.getClusterId()));
        if (envUsedOptional.isPresent()) {
            envAvailableResource.setCpus((record.getCpus().subtract(envUsedOptional.get().getCpu())).max(ZERO));
            envAvailableResource.setMemory((record.getMemory().subtract(envUsedOptional.get().getMemory())).max(ZERO));
            envAvailableResource.setDisk((record.getDisk().subtract(envUsedOptional.get().getDisk())).max(ZERO));
        }
        return envAvailableResource;
    }

    @Override
    public ClusterEnvResourceDto sumClusterUsedResource(Long clusterId) {

        ClusterEnvResourceDto clusterEnvResource = new ClusterEnvResourceDto();
        List<RmsEnvEntity> envs = this.mapper.selectByClusterId(clusterId);
        if (isEmpty(envs)) {
            return clusterEnvResource;
        }
        clusterEnvResource.setEnvNum(envs.size());
        clusterEnvResource.setCpus(envs.stream().map(RmsEnvEntity::getCpus).reduce(ZERO, BigDecimal::add));
        clusterEnvResource.setMemory(envs.stream().map(RmsEnvEntity::getMemory).reduce(ZERO, BigDecimal::add));
        clusterEnvResource.setDisk(envs.stream().map(RmsEnvEntity::getDisk).reduce(ZERO, BigDecimal::add));
        return clusterEnvResource;
    }

    @Override
    public List<EnvResourceDto> listClusterEnvUsedResource(Long clusterId) {

        List<RmsEnvEntity> envs = this.mapper.selectByClusterId(clusterId);
        if (isEmpty(envs)) {
            return newArrayList();
        }
        return envs.stream().map(this::convertToEnvResource).collect(toList());
    }

    private EnvResourceDto convertToEnvResource(RmsEnvEntity record) {
        return copyProperty(record, EnvResourceDto.class);
    }

    @Override
    public ClusterAppServiceInfoDto getClusterAppServiceInfo(EnvAppReq envAppReq) {

        RmsEnvEntity record = this.selectEntityByPrimaryKey(envAppReq.getEnvId());
        assertTrue(isNull(record), ENV_NOT_EXISTS);
        return clusterAppService.getClusterAppServiceInfo(record.getClusterId(), getAppIdFormat(record.getCode(), envAppReq.getAppId()));
    }

    @Override
    public List<ClusterAppServiceHostDto> getClusterAppServiceHosts(EnvAppReq envAppReq) {
        RmsEnvEntity record = this.selectEntityByPrimaryKey(envAppReq.getEnvId());
        assertTrue(isNull(record), ENV_NOT_EXISTS);
        return clusterAppService.getClusterAppServiceHosts(record.getClusterId(), getAppIdFormat(record.getCode(), envAppReq.getAppId()));
    }

    @Override
    public String getClusterAppImage(EnvAppReq envAppReq) {
        RmsEnvEntity record = this.selectEntityByPrimaryKey(envAppReq.getEnvId());
        assertTrue(isNull(record), ENV_NOT_EXISTS);
        return clusterAppService.getClusterAppImage(record.getClusterId(), getAppIdFormat(record.getCode(), envAppReq.getAppId()));
    }

    @Override
    public List<ClusterDockerDto> getClusterDockers(EnvAppReq envAppReq) {
        RmsEnvEntity record = this.selectEntityByPrimaryKey(envAppReq.getEnvId());
        assertTrue(isNull(record), ENV_NOT_EXISTS);
        return clusterService.getClusterDockers(record.getClusterId(), getAppIdFormat(record.getCode(), envAppReq.getAppId()));
    }

    private String getAppIdFormat(String envCode, String appIdSuffix) {
        if (appIdSuffix.startsWith("/")) {
            return format(APP_ID_FORMAT, envCode, appIdSuffix.substring(1));
        }
        return format(APP_ID_FORMAT, envCode, appIdSuffix);
    }

    @Override
    public List<EnvDto> list(EnvQueryReq req) {

        List<RmsEnvEntity> envs = this.mapper.selectByCondition(req.getIds(), req.getKeyword());
        if (isEmpty(envs)) {
            return null;
        }
        return envs.stream().map(this::convertToEnvDto).collect(toList());
    }

    private EnvDto convertToEnvDto(RmsEnvEntity record) {

        EnvDto env = copyProperty(record, EnvDto.class);
        if (isNull(env)) {
            return null;
        }
        // get cluster info
        RmsClusterDto cluster = clusterService.selectByPrimaryKey(record.getClusterId());
        env.setClusterId(cluster.getId());
        env.setClusterName(cluster.getName());
        env.setState(getEnvState(env.getState(), cluster.getStatus()));
        String clusterType = clusterService.getClusterInfo(cluster.getId()).getType();
        env.setClusterType(clusterType);
        // get env used resource from container service
        Optional<ClusterAppResourceDto> envUsedOptional = ofNullable(clusterAppService.getClusterAppResource(record.getCode(), cluster.getId()));
        if (envUsedOptional.isPresent()) {
            env.setCpusUsed(envUsedOptional.get().getCpu());
            env.setMemoryUsed(envUsedOptional.get().getMemory());
            env.setDiskUsed(envUsedOptional.get().getDisk());
        }
        return env;
    }

    private String getEnvState(String envState, String clusterState) {

        if (ClusterStatusEnum.ABNORMAL.getCode().equals(clusterState)) {
            return clusterState;
        }

        return envState;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(EnvCreateReq req) {

        // check params
        validateCreateParams(req);

        // check resource
        validateClusterResourceAvailable(req.getClusterId(), req.getCpus(), req.getMemory(), req.getDisk());

        // save env info
        RmsEnvEntity entity = buildEnvEntity(req.getClusterId(), req.getCpus(), req.getMemory(), req.getDisk(), NORMAL.getState());
        entity.setName(req.getEnvName());
        entity.setCode(req.getEnvCode());
        this.insertSelective(entity);

        // save env log info
        saveEnvLog(entity, OptType.CREATE);
    }

    private void validateClusterResourceAvailable(Long clusterId, BigDecimal cpus, BigDecimal memory, BigDecimal disk) {

        // cluster available resource
        ClusterAppResourceDto clusterAvailableResource = clusterService.getClusterAvailableResource(clusterId);
        assertTrue(isNull(clusterAvailableResource), CLUSTER_RESOURCE_UNAVAILABLE);
        // check resource available
        validateClusterResourceAvailable(clusterAvailableResource, cpus, memory, disk);
    }

    private void validateClusterResourceAvailable(ClusterAppResourceDto clusterAvailableResource, BigDecimal cpus, BigDecimal memory, BigDecimal disk) {

        assertTrue(defaultValue(cpus, ZERO).compareTo(defaultValue(clusterAvailableResource.getCpu(), ZERO)) > 0, CLUSTER_CPU_UNAVAILABLE);
        assertTrue(defaultValue(memory, ZERO).compareTo(defaultValue(clusterAvailableResource.getMemory(), ZERO)) > 0, CLUSTER_MEMORY_UNAVAILABLE);
        assertTrue(defaultValue(disk, ZERO).compareTo(defaultValue(clusterAvailableResource.getDisk(), ZERO)) > 0, CLUSTER_DISK_UNAVAILABLE);
    }

    private void validateCreateParams(EnvCreateReq req) {

        // check existence of env code or name
        RmsEnvEntity record = this.mapper.selectByNameOrCode(req.getEnvName(), req.getEnvCode());
        if (isNull(record)) {
            return;
        }
        assertTrue(equalsIgnoreCase(req.getEnvCode(), record.getCode()), ENV_CODE_EXISTS);
        assertTrue(equalsIgnoreCase(req.getEnvName(), record.getName()), ENV_NAME_EXISTS);
    }

    private RmsEnvEntity buildEnvEntity(Long clusterId, BigDecimal cpus, BigDecimal memory, BigDecimal disk, String envState) {

        RmsEnvEntity entity = new RmsEnvEntity();

        entity.setClusterId(clusterId);
        entity.setCpus(cpus);
        entity.setMemory(memory);
        entity.setDisk(disk);
        entity.setState(envState);

        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(EnvModifyReq req) {

        // select env info
        RmsEnvEntity record = this.selectEntityByPrimaryKey(req.getEnvId());
        assertTrue(isNull(record), ENV_NOT_EXISTS);

        // check resource
        validateClusterResourceAvailable(record, req.getClusterId(), req.getCpus(), req.getMemory(), req.getDisk());

        // update env info
        RmsEnvEntity entity = buildEnvEntity(req.getClusterId(), req.getCpus(), req.getMemory(), req.getDisk(), req.getEnvState());
        entity.setId(req.getEnvId());
        this.updateByPrimaryKeySelective(entity);

        // save env log info
        saveEnvLog(entity, OptType.UPDATE);
    }

    private void validateClusterResourceAvailable(RmsEnvEntity env, Long clusterId, BigDecimal cpus, BigDecimal memory, BigDecimal disk) {

        // cluster available resource
        ClusterAppResourceDto clusterAvailableResource = clusterService.getClusterAvailableResource(clusterId);
        assertTrue(isNull(clusterAvailableResource), CLUSTER_RESOURCE_UNAVAILABLE);
        if (isClusterUnChanged(env.getClusterId(), clusterId)) {
            // check env minimum resource
            validateEnvResourceMinimum(env, cpus, memory, disk);
            clusterAvailableResource.setCpu(defaultValue(clusterAvailableResource.getCpu(), ZERO).add(defaultValue(env.getCpus(), ZERO)));
            clusterAvailableResource.setMemory(defaultValue(clusterAvailableResource.getMemory(), ZERO).add(defaultValue(env.getMemory(), ZERO)));
            clusterAvailableResource.setDisk(defaultValue(clusterAvailableResource.getDisk(), ZERO).add(defaultValue(env.getDisk(), ZERO)));
        }
        // check resource available
        validateClusterResourceAvailable(clusterAvailableResource, cpus, memory, disk);
    }

    private boolean isClusterUnChanged(Long srcClusterId, Long destClusterId) {
        return srcClusterId.equals(destClusterId);
    }

    private void validateEnvResourceMinimum(RmsEnvEntity env, BigDecimal cpus, BigDecimal memory, BigDecimal disk) {

        // get env used resource from container service
        Optional<ClusterAppResourceDto> envUsedOptional = ofNullable(clusterAppService.getClusterAppResource(env.getCode(), env.getId()));
        if (!envUsedOptional.isPresent()) {
            return;
        }
        ClusterAppResourceDto envUsed = envUsedOptional.get();
        assertTrue(defaultValue(cpus, ZERO).compareTo(defaultValue(envUsed.getCpu(), ZERO)) < 0, ENV_CPU_LESS_THAN_USED);
        assertTrue(defaultValue(memory, ZERO).compareTo(defaultValue(envUsed.getMemory(), ZERO)) < 0, ENV_MEMORY_LESS_THAN_USED);
        assertTrue(defaultValue(disk, ZERO).compareTo(defaultValue(envUsed.getDisk(), ZERO)) < 0, ENV_DISK_LESS_THAN_USED);
    }

    @Override
    public EnvDto get(Long envId) {

        // select env info
        RmsEnvEntity record = this.selectEntityByPrimaryKey(envId);
        assertTrue(isNull(record), ENV_NOT_EXISTS);
        return convertToEnvDto(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long envId) {

        // select env info
        RmsEnvEntity record = this.selectEntityByPrimaryKey(envId);
        assertTrue(isNull(record), ENV_NOT_EXISTS);

        // delete env info
        this.deleteByPrimaryKey(envId);

        // save env log info
        saveEnvLog(record, OptType.DELETE);
    }

    private void saveEnvLog(RmsEnvEntity envEntity, OptType optType) {

        RmsEnvLogEntity entity = new RmsEnvLogEntity();

        entity.setEnvId(envEntity.getId());
        entity.setClusterId(envEntity.getClusterId());
        entity.setCpus(envEntity.getCpus());
        entity.setMemory(envEntity.getMemory());
        entity.setDisk(envEntity.getDisk());
        entity.setState(envEntity.getState());
        entity.setOptType(optType.getType());

        envLogMapper.insertSelective(entity);
    }
}