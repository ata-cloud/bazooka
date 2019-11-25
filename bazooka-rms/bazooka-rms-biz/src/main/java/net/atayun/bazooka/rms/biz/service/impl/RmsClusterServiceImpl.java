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

import com.youyu.common.api.PageData;
import com.youyu.common.exception.BizException;
import com.youyu.common.service.AbstractService;
import com.youyu.common.utils.YyAssert;
import lombok.extern.slf4j.Slf4j;
import mesosphere.marathon.client.model.v2.Task;
import net.atayun.bazooka.base.dcos.DcosServerBean;
import net.atayun.bazooka.base.dcos.api.DcosApi;
import net.atayun.bazooka.base.dcos.api.model.GetLogInfoResponse;
import net.atayun.bazooka.base.dcos.api.model.GetSlaveStateResponse;
import net.atayun.bazooka.base.dcos.dto.*;
import net.atayun.bazooka.rms.api.dto.*;
import net.atayun.bazooka.rms.api.dto.req.ClusterDetailReqDto;
import net.atayun.bazooka.rms.api.dto.req.ClusterDockerInstanceLogReqDto;
import net.atayun.bazooka.rms.api.dto.req.ClusterReqDto;
import net.atayun.bazooka.rms.api.dto.rsp.*;
import net.atayun.bazooka.rms.api.enums.LogTypeEnum;
import net.atayun.bazooka.rms.api.param.CreateClusterReq;
import net.atayun.bazooka.rms.api.param.SingleNodeReq;
import net.atayun.bazooka.rms.biz.component.strategy.cluster.ClusterComponentRefreshStrategy;
import net.atayun.bazooka.rms.biz.dal.dao.RmsClusterAppMapper;
import net.atayun.bazooka.rms.biz.dal.dao.RmsClusterConfigMapper;
import net.atayun.bazooka.rms.biz.dal.dao.RmsClusterMapper;
import net.atayun.bazooka.rms.biz.dal.dao.RmsClusterNodeMapper;
import net.atayun.bazooka.rms.biz.dal.entity.RmsClusterConfigEntity;
import net.atayun.bazooka.rms.biz.dal.entity.RmsClusterEntity;
import net.atayun.bazooka.rms.biz.dal.entity.RmsClusterNodeEntity;
import net.atayun.bazooka.rms.biz.enums.ClusterTypeEnum;
import net.atayun.bazooka.rms.biz.service.EnvService;
import net.atayun.bazooka.rms.biz.service.RmsClusterConfigService;
import net.atayun.bazooka.rms.biz.service.RmsClusterService;
import org.apache.ibatis.annotations.Options;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.alibaba.fastjson.JSON.parseArray;
import static com.alibaba.fastjson.JSON.parseObject;
import static java.lang.Long.parseLong;
import static java.lang.Math.min;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.stream.Collectors.toList;
import static net.atayun.bazooka.base.bean.StrategyNumBean.getBeanInstance;
import static net.atayun.bazooka.base.constant.CommonConstants.MARATHON_PORT;
import static net.atayun.bazooka.base.constant.CommonConstants.PROTOCOL;
import static net.atayun.bazooka.base.utils.EnumUtil.getEnum;
import static net.atayun.bazooka.base.utils.OrikaCopyUtil.copyProperty;
import static net.atayun.bazooka.base.utils.OrikaCopyUtil.copyProperty4List;
import static net.atayun.bazooka.base.utils.StringUtil.eq;
import static net.atayun.bazooka.rms.api.enums.ClusterTaskHealthEnum.*;
import static net.atayun.bazooka.rms.api.enums.LogTypeEnum.STDERR;
import static net.atayun.bazooka.rms.api.enums.LogTypeEnum.STDOUT;
import static net.atayun.bazooka.rms.api.enums.RmsResultCode.GET_INSTANCE_LOG_EXCEPTION;
import static net.atayun.bazooka.rms.biz.enums.ClusterConfigTypeEnum.*;
import static net.atayun.bazooka.rms.biz.enums.ClusterStatusEnum.NORMAL;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;
import static org.apache.commons.lang3.StringUtils.*;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 资源集群 service impl
 */
@Slf4j
@Service
public class RmsClusterServiceImpl extends AbstractService<Long, RmsClusterDto, RmsClusterEntity, RmsClusterMapper> implements RmsClusterService {

    /**
     * 核心数
     */
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    /**
     * 集群刷新线程
     */
    private static final ThreadPoolExecutor REFRESH_CLUSTER_THREAD_POOL = new ThreadPoolExecutor(NUMBER_OF_CORES + 1, NUMBER_OF_CORES * 2, 1, HOURS, new LinkedBlockingQueue<>(100), new CustomizableThreadFactory("refreshCluster-"));

    @Autowired
    private DcosServerBean dcosServerBean;

    @Autowired
    private EnvService envService;
    @Autowired
    private RmsClusterConfigService rmsClusterConfigService;

    @Autowired
    private RmsClusterMapper rmsClusterMapper;
    @Autowired
    private RmsClusterConfigMapper rmsClusterConfigMapper;
    @Autowired
    private RmsClusterNodeMapper rmsClusterNodeMapper;
    @Autowired
    private RmsClusterAppMapper rmsClusterAppMapper;

    @Override
    public void refreshClusterInfo(RmsClusterService rmsClusterService) {
        List<RmsClusterEntity> rmsClusterEntities = rmsClusterMapper.selectAll();
        for (RmsClusterEntity rmsClusterEntity : rmsClusterEntities) {
            REFRESH_CLUSTER_THREAD_POOL.execute(() -> rmsClusterService.refreshClusterInfo(rmsClusterEntity));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshClusterInfo(RmsClusterEntity rmsClusterEntity) {
        List<RmsClusterConfigEntity> rmsClusterConfigEntities = rmsClusterConfigMapper.getByClusterId(rmsClusterEntity.getId());
        ClusterComponentRefreshStrategy clusterComponentRefreshStrategy = getBeanInstance(ClusterComponentRefreshStrategy.class, rmsClusterEntity.getType());
        clusterComponentRefreshStrategy.refreshClusterInfo(rmsClusterEntity, rmsClusterConfigEntities);
    }

    @Override
    public TaskDto getTask(Long clusterId) {
        return doGetRunningTask(clusterId);
    }

    @Override
    public MesosTaskDto getMesosTask(Long clusterId) {
        return doGetMesosTaskDto(clusterId);
    }

    @Override
    public PageData<ClusterRspDto> getClusterPage(ClusterReqDto clusterReqDto) {
        PageData<ClusterRspDto> pageData = new PageData<>(clusterReqDto.getPageNo(), clusterReqDto.getPageSize());

        List<ClusterRspDto> clusters = new ArrayList<>();
        List<RmsClusterEntity> rmsClusterEntities = rmsClusterMapper.selectAllInPage(pageData);
        for (RmsClusterEntity rmsClusterEntity : rmsClusterEntities) {
            ClusterRspDto clusterRspDto = copyProperty(rmsClusterEntity, ClusterRspDto.class);

            fillClusterRsp(rmsClusterEntity, clusterRspDto);
            clusters.add(clusterRspDto);
        }
        pageData.setRows(clusters);
        return pageData;
    }

    @Override
    public ClusterAppResourceDto getClusterAvailableResource(Long clusterId) {
        return doGetClusterAppResourceDto(clusterId);
    }

    @Override
    public ClusterDetailRspDto getClusterDetail(ClusterDetailReqDto clusterDetailReqDto) {
        Long clusterId = clusterDetailReqDto.getClusterId();
        RmsClusterEntity rmsClusterEntity = rmsClusterMapper.selectByPrimaryKey(clusterId);
        ClusterDetailRspDto clusterDetailRspDto = copyProperty(rmsClusterEntity, ClusterDetailRspDto.class);
        List<RmsClusterConfigEntity> rmsClusterConfigEntities = rmsClusterConfigMapper.getByClusterId(clusterId);

        clusterDetailRspDto.setMarathons(copyProperty4List(rmsClusterConfigEntities.stream().filter(r -> eq(r.getType(), MARATHON.getCode())).collect(toList()), ClusterConfigDetailRspDto.class));
        clusterDetailRspDto.setMlbs(copyProperty4List(rmsClusterConfigEntities.stream().filter(r -> eq(r.getType(), MLB.getCode())).collect(toList()), ClusterConfigDetailRspDto.class));
        clusterDetailRspDto.setDockerHubs(copyProperty4List(rmsClusterConfigEntities.stream().filter(r -> eq(r.getType(), DOCKER_HUB.getCode())).collect(toList()), ClusterConfigDetailRspDto.class));

        List<EnvResourceDto> envResources = envService.listClusterEnvUsedResource(clusterId);
        //增加未使用的资源信息
        EnvResourceDto unusedEnvResource = getClusterUnusedResource(clusterId);
        envResources.add(unusedEnvResource);

        clusterDetailRspDto.setEnvResources(envResources);
        return clusterDetailRspDto;
    }

    @Override
    public List<ClusterDockerDto> getClusterDockers(Long clusterId, String appId) {
        MesosTaskDto mesosTask = getMesosTask(clusterId);
        if (isNull(mesosTask) || isEmpty(mesosTask.getTasks())) {
            return null;
        }

        List<MesosTaskInfoDto> taskInfos = mesosTask.getTasks().stream().filter(r -> eq(r.getName(), transferAppId2TaskName(appId))).collect(toList());
        if (isEmpty(taskInfos)) {
            return null;
        }

        List<TaskInfoDto> runningTasks = getRunningTasksByClusterIdAppId(clusterId, appId);

        return doGetClusterDockers(taskInfos, runningTasks, clusterId);
    }

    @Override
    public List<ClusterMarathonConfigRspDto> getClusterMarathonConfigs() {
        List<RmsClusterEntity> rmsClusterEntities = rmsClusterMapper.selectAll();

        List<ClusterMarathonConfigRspDto> clusterMarathonConfigs = new ArrayList<>();
        for (RmsClusterEntity rmsClusterEntity : rmsClusterEntities) {
            ClusterMarathonConfigRspDto clusterMarathonConfig = getClusterMarathonConfig(rmsClusterEntity);
            clusterMarathonConfigs.add(clusterMarathonConfig);
        }
        return clusterMarathonConfigs;
    }

    @Override
    public ClusterDockerInstanceLogRspDto getClusterDockerInstanceLog(ClusterDockerInstanceLogReqDto clusterDockerInstanceLogReqDto) {
        String slaveId = clusterDockerInstanceLogReqDto.getSlaveId();
        ClusterConfigDto clusterConfig = rmsClusterConfigService.getClusterConfig(clusterDockerInstanceLogReqDto.getClusterId());
        try {
            DcosApi dcos = dcosServerBean.getInstance(PROTOCOL + clusterConfig.getDcosEndpoint());
            Integer offset = clusterDockerInstanceLogReqDto.getOffset();
            Integer maxLogOffset = getInstanceLogOffset(clusterDockerInstanceLogReqDto, clusterConfig);
            if (nonNull(offset) && offset >= maxLogOffset) {
                ClusterDockerInstanceLogRspDto clusterDockerInstanceLogRspDto = new ClusterDockerInstanceLogRspDto();
                clusterDockerInstanceLogRspDto.setUpOffset(clusterDockerInstanceLogReqDto.getUpOffset());
                clusterDockerInstanceLogRspDto.setDownOffset(maxLogOffset);
                return clusterDockerInstanceLogRspDto;
            }

            int length = clusterDockerInstanceLogReqDto.getLength();
            Integer instanceLogOffset;
            if (nonNull(offset)) {
                instanceLogOffset = offset;
            } else {
                if (maxLogOffset >= length) {
                    instanceLogOffset = maxLogOffset - clusterDockerInstanceLogReqDto.getLength();
                } else {
                    instanceLogOffset = 0;
                    length = maxLogOffset;
                }
            }
            instanceLogOffset = instanceLogOffset < 0 ? 0 : instanceLogOffset;
            GetLogInfoResponse getLogInfoResponse = dcos.getAppLogInfo(slaveId, getDockerInstanceLogPath(dcos, slaveId, clusterDockerInstanceLogReqDto), instanceLogOffset, length);
            ClusterDockerInstanceLogRspDto clusterDockerInstanceLogRspDto = new ClusterDockerInstanceLogRspDto();
            clusterDockerInstanceLogRspDto.setLog(getLogInfoResponse.getData());

            if (isNull(offset)) {
                clusterDockerInstanceLogRspDto.setUpOffset(instanceLogOffset);
                int maxOffset = instanceLogOffset + clusterDockerInstanceLogReqDto.getLength();
                if (maxOffset > maxLogOffset) {
                    maxOffset = maxLogOffset;
                }
                clusterDockerInstanceLogRspDto.setDownOffset(maxOffset);
            } else {
                clusterDockerInstanceLogRspDto.setUpOffset(clusterDockerInstanceLogReqDto.getUpOffset());
                clusterDockerInstanceLogRspDto.setDownOffset(min(clusterDockerInstanceLogReqDto.getDownOffset(), maxLogOffset));
            }
            return clusterDockerInstanceLogRspDto;
        } catch (Exception ex) {
            log.error("获取实例日志异常信息:[{}]", ex.getMessage());
            throw new BizException(GET_INSTANCE_LOG_EXCEPTION);
        }
    }

    /**
     * 获取日志偏移量
     *
     * @param clusterDockerInstanceLogReqDto
     * @param clusterConfig
     * @return
     */
    private Integer getInstanceLogOffset(ClusterDockerInstanceLogReqDto clusterDockerInstanceLogReqDto, ClusterConfigDto clusterConfig) {
        String slaveId = clusterDockerInstanceLogReqDto.getSlaveId();
        String frameworkId = clusterDockerInstanceLogReqDto.getFrameworkId();
        String taskId = clusterDockerInstanceLogReqDto.getTaskId();
        String containerId = clusterDockerInstanceLogReqDto.getContainerId();
        //参考dcos日志网页请求地址
        String url = join(PROTOCOL, clusterConfig.getDcosEndpoint(), "/agent/", slaveId, "/files/browse?_timestamp=", currentTimeMillis(), "&path=/var/lib/mesos/slave/slaves/", slaveId, "/frameworks/", frameworkId, "/executors/", taskId, "/runs/", containerId, "/");
        String json = new RestTemplate().getForObject(url, String.class);
        List<MesosInstanceLogDto> mesosInstanceLogs = parseArray(json, MesosInstanceLogDto.class);
        MesosInstanceLogDto mesosInstanceLogDto = mesosInstanceLogs.stream().filter(r -> {
            LogTypeEnum anEnum = getEnum(LogTypeEnum.class, clusterDockerInstanceLogReqDto.getLogType());
            String path = anEnum.getPath();
            return endsWith(r.getPath(), path);
        }).findFirst().get();
        return mesosInstanceLogDto.getSize();
    }

    /**
     * 获取集群信息
     *
     * @param clusterName
     * @return
     */
    @Override
    public ClusterComponentsDto getClusterComponentsInfo(String clusterName) {
        //查询集群
        RmsClusterEntity clusterEntity = new RmsClusterEntity();
        clusterEntity.setName(clusterName);
        clusterEntity = this.selectOneEntity(clusterEntity);
        YyAssert.paramCheck(ObjectUtils.isEmpty(clusterEntity), "集群不存在");

        ClusterComponentsDto clusterComponentsDto = new ClusterComponentsDto(clusterEntity.getClusterId(), clusterEntity.getName(), null);

        Example example = Example.builder(RmsClusterConfigEntity.class).build();
        example.createCriteria().andEqualTo("clusterId", clusterEntity.getClusterId());
        List<RmsClusterConfigEntity> clusterConfigEntityList = this.rmsClusterConfigService.selectEntityByExample(example);

        if (!ObjectUtils.isEmpty(clusterConfigEntityList)) {
            //如果某个组件有多个的话，选一条组件记录（如DC/OS会配多个，但只要返回一个）
            List<ClusterComponent> clusterComponentList = clusterConfigEntityList
                    .stream()
                    .map((config -> new ClusterComponent(RmsClusterConfigEntity.getTypeName(config.getType()), config.getVersion(), config.getStatus(), config.getUrl())))
                    .collect(Collectors.groupingBy(ClusterComponent::getName))
                    .entrySet().stream()
                    .map(entry -> entry.getValue().get(0))
                    .collect(toList());

            clusterComponentsDto.setClusterComponents(clusterComponentList);

        }

        return clusterComponentsDto;
    }

    /**
     * 获取容器实例日志路径
     *
     * @param dcos
     * @param slaveId
     * @param clusterDockerInstanceLogReqDto
     * @return
     */
    private String getDockerInstanceLogPath(DcosApi dcos, String slaveId, ClusterDockerInstanceLogReqDto clusterDockerInstanceLogReqDto) {
        String path = eq(STDOUT.getCode(), clusterDockerInstanceLogReqDto.getLogType()) ? STDOUT.getPath() : STDERR.getPath();
        Collection<Task> tasks = dcos.getTasks().getTasks();
        if (isEmpty(tasks)) {
            return null;
        }

        Optional<Task> taskOptional = tasks.stream().filter(t -> eq(t.getId(), clusterDockerInstanceLogReqDto.getTaskId())).findFirst();
        if (!taskOptional.isPresent()) {
            return null;
        }

        List<GetSlaveStateResponse.Framework> frameworks = dcos.getSlaveState(slaveId).getFrameworks();
        for (GetSlaveStateResponse.Framework framework : frameworks) {
            if ("marathon".equals(framework.getName())) {
                for (GetSlaveStateResponse.Framework.Executor executor : framework.getExecutors()) {
                    if (taskOptional.get().getId().equals(executor.getId())) {
                        return executor.getDirectory() + path;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 填充集群响应信息
     *
     * @param rmsClusterEntity
     * @param clusterRspDto
     */
    private void fillClusterRsp(RmsClusterEntity rmsClusterEntity, ClusterRspDto clusterRspDto) {
        ClusterEnvResourceDto clusterEnvResourceDto = envService.sumClusterUsedResource(rmsClusterEntity.getId());
        clusterRspDto.setEnvQuantity(clusterEnvResourceDto.getEnvNum());
        clusterRspDto.setEnvCpu(clusterEnvResourceDto.getCpus());
        clusterRspDto.setEnvMemory(clusterEnvResourceDto.getMemory());
        clusterRspDto.setEnvDisk(clusterEnvResourceDto.getDisk());

        //查询正常和所有的节点数量
        List<Integer> nodeQuantities = rmsClusterNodeMapper.getNormalAndAllClusterNodeQuantity(rmsClusterEntity.getId());
        clusterRspDto.setNormalNodeQuantity(nodeQuantities.get(0));
        clusterRspDto.setNodeQuantity(nodeQuantities.get(1));

        //查询运行中和所有的服务数量
        List<Integer> serviceQuantities = rmsClusterAppMapper.getRunningAndAllClusterAppQuantity(rmsClusterEntity.getId());
        clusterRspDto.setRunningServiceQuantity(serviceQuantities.get(0));
        clusterRspDto.setServiceQuantity(serviceQuantities.get(1));
    }

    /**
     * 查询集群Marathon配置信息
     *
     * @param clusterId
     * @return
     */
    private RmsClusterConfigEntity getMarathonClusterConfig(Long clusterId) {
        RmsClusterConfigEntity marathonConfigQuery = new RmsClusterConfigEntity();
        marathonConfigQuery.setClusterId(clusterId);
        marathonConfigQuery.setStatus(NORMAL.getCode());
        marathonConfigQuery.setType(MARATHON.getCode());
        List<RmsClusterConfigEntity> rmsClusterConfigEntities = rmsClusterConfigMapper.select(marathonConfigQuery);
        Optional<RmsClusterConfigEntity> clusterConfigOptional = rmsClusterConfigEntities.stream().findFirst();
        return clusterConfigOptional.isPresent() ? clusterConfigOptional.get() : null;
    }

    /**
     * 获取集群容器Mesos task信息
     *
     * @param clusterId
     * @return
     */
    private MesosTaskDto doGetMesosTaskDto(Long clusterId) {
        RmsClusterConfigEntity rmsClusterConfigEntity = getMarathonClusterConfig(clusterId);
        if (isNull(rmsClusterConfigEntity)) {
            return null;
        }

        RmsClusterEntity rmsClusterEntity = rmsClusterMapper.selectByPrimaryKey(clusterId);
        ClusterComponentRefreshStrategy clusterComponentRefreshStrategy = getBeanInstance(ClusterComponentRefreshStrategy.class, rmsClusterEntity.getType());
        return clusterComponentRefreshStrategy.getMesosTask(rmsClusterConfigEntity);
    }

    /**
     * 获取集群未使用的资源(总资源减去环境已使用资源)
     *
     * @param clusterId
     * @return
     */
    private EnvResourceDto getClusterUnusedResource(Long clusterId) {
        ClusterAppResourceDto clusterAppResource = doGetClusterAppResourceDto(clusterId);

        EnvResourceDto envResource = new EnvResourceDto();
        envResource.setCpus(clusterAppResource.getCpu());
        envResource.setMemory(clusterAppResource.getMemory());
        envResource.setDisk(clusterAppResource.getDisk());
        return envResource;
    }

    /**
     * 执行获取集群可用资源
     *
     * @param clusterId
     * @return
     */
    private ClusterAppResourceDto doGetClusterAppResourceDto(Long clusterId) {
        ClusterAppResourceDto clusterAvailableResource = new ClusterAppResourceDto();
        RmsClusterEntity record = rmsClusterMapper.selectByPrimaryKey(clusterId);
        if (isNull(record)) {
            return clusterAvailableResource;
        }
        // sum env resource
        ClusterEnvResourceDto sumEnvResource = envService.sumClusterUsedResource(clusterId);
        clusterAvailableResource.setCpu((record.getCpu().subtract(sumEnvResource.getCpus())).max(ZERO));
        clusterAvailableResource.setMemory((record.getMemory().subtract(sumEnvResource.getMemory())).max(ZERO));
        clusterAvailableResource.setDisk((record.getDisk().subtract(sumEnvResource.getDisk())).max(ZERO));
        return clusterAvailableResource;
    }

    /**
     * 执行获取集群容器列表信息
     *
     * @param taskInfos
     * @param runningTasks
     * @param clusterId
     * @return
     */
    private List<ClusterDockerDto> doGetClusterDockers(List<MesosTaskInfoDto> taskInfos, List<TaskInfoDto> runningTasks, Long clusterId) {
        List<ClusterDockerDto> clusterDockers = new ArrayList<>();
        for (MesosTaskInfoDto mesosTaskInfoDto : taskInfos) {
            clusterDockers.add(getClusterDocker(mesosTaskInfoDto, runningTasks, clusterId));
        }
        return clusterDockers;
    }

    /**
     * 获取集群容器列表
     *
     * @param mesosTaskInfoDto
     * @param runningTasks
     * @param clusterId
     * @return
     */
    private ClusterDockerDto getClusterDocker(MesosTaskInfoDto mesosTaskInfoDto, List<TaskInfoDto> runningTasks, Long clusterId) {
        ClusterDockerDto clusterDocker = new ClusterDockerDto();
        clusterDocker.setClusterId(clusterId);
        clusterDocker.setTaskId(mesosTaskInfoDto.getId());
        clusterDocker.setStatus(mesosTaskInfoDto.getState());
        clusterDocker.setCpu(mesosTaskInfoDto.getResources().getCpus());
        clusterDocker.setMemory(mesosTaskInfoDto.getResources().getMem());
        clusterDocker.setSlaveId(mesosTaskInfoDto.getSlaveId());
        clusterDocker.setFrameworkId(mesosTaskInfoDto.getFrameworkId());
        String host = rmsClusterNodeMapper.getHostByNodeId(mesosTaskInfoDto.getSlaveId());
        clusterDocker.setHost(host);

        List<MesosTaskContainerDockerPortMappingDto> portMappings = mesosTaskInfoDto.getContainer().getDocker().getPortMappings();
        if (!isEmpty(portMappings)) {
            clusterDocker.setPorts(portMappings.stream().map(r -> r.getHostPort()).collect(toList()));
        }

        List<MesosTaskStatusesDto> statuses = mesosTaskInfoDto.getStatuses();
        if (!isEmpty(statuses)) {
            MesosTaskStatusesDto mesosTaskStatus = statuses.get(statuses.size() - 1);
            String timestamp = valueOf(parseLong(substringBefore(mesosTaskStatus.getTimestamp(), ".")) * 1000);
            clusterDocker.setUpdateTime(parseObject(timestamp, Date.class));
            clusterDocker.setContainerId(getContainerId(mesosTaskStatus));
        }
        clusterDocker.setHealthStatus(getHealthStatus(runningTasks, mesosTaskInfoDto.getId(), mesosTaskInfoDto.getState()));
        return clusterDocker;
    }

    /**
     * 获取容器id
     *
     * @param mesosTaskStatus
     * @return
     */
    private String getContainerId(MesosTaskStatusesDto mesosTaskStatus) {
        try {
            return mesosTaskStatus.getContainerStatus().getContainerId().getValue();
        } catch (Exception ex) {
            log.error("获取容器id一次信息:[{}]", getFullStackTrace(ex));
            return null;
        }
    }

    /**
     * 获取task健康状态
     *
     * @param runningTasks
     * @param id
     * @param state
     * @return
     */
    private String getHealthStatus(List<TaskInfoDto> runningTasks, String id, String state) {
        if (!eq(state, "TASK_RUNNING")) {
            return NOT_EXIST_HEALTH.getCode();
        }

        if (isEmpty(runningTasks)) {
            return NOT_EXIST_HEALTH.getCode();
        }

        Optional<TaskInfoDto> taskInfoDtoOptional = runningTasks.stream().filter(r -> eq(r.getId(), id)).findFirst();
        if (!taskInfoDtoOptional.isPresent()) {
            return NOT_EXIST_HEALTH.getCode();
        }

        List<TaskHealthDto> healthCheckResults = taskInfoDtoOptional.get().getHealthCheckResults();
        if (isEmpty(healthCheckResults)) {
            return NO_HEALTH_CHECK.getCode();
        }

        boolean allHealth = healthCheckResults.stream().allMatch(r -> r.getAlive());
        if (allHealth) {
            return HEALTH.getCode();
        }

        return UN_HEALTH.getCode();
    }

    /**
     * 获取集群Marathon配置详情
     *
     * @param rmsClusterEntity
     * @return
     */
    private ClusterMarathonConfigRspDto getClusterMarathonConfig(RmsClusterEntity rmsClusterEntity) {
        ClusterMarathonConfigRspDto clusterMarathonConfig = new ClusterMarathonConfigRspDto();
        clusterMarathonConfig.setClusterId(rmsClusterEntity.getId());
        clusterMarathonConfig.setEnableMonitor(rmsClusterEntity.getEnableMonitor());
        clusterMarathonConfig.setClusterType(rmsClusterEntity.getType());

        String url = rmsClusterConfigMapper.getOneValidMarathonUrlByClusterId(rmsClusterEntity.getId());
        clusterMarathonConfig.setUrl(url);
        return clusterMarathonConfig;
    }

    /**
     * 转换appId to task Name
     *
     * @param appId
     * @return
     */
    private String transferAppId2TaskName(String appId) {
        String[] split = split(appId, "/");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = split.length - 1; i >= 0; i--) {
            stringBuilder.append(split[i]).append(".");
        }
        return substringBeforeLast(stringBuilder.toString(), ".");
    }

    /**
     * 获取running的task列表
     *
     * @param clusterId
     * @return
     */
    private TaskDto doGetRunningTask(Long clusterId) {
        RmsClusterConfigEntity rmsClusterConfigEntity = getMarathonClusterConfig(clusterId);
        if (isNull(rmsClusterConfigEntity)) {
            return null;
        }

        RmsClusterEntity rmsClusterEntity = rmsClusterMapper.selectByPrimaryKey(clusterId);
        ClusterComponentRefreshStrategy clusterComponentRefreshStrategy = getBeanInstance(ClusterComponentRefreshStrategy.class, rmsClusterEntity.getType());
        return clusterComponentRefreshStrategy.getTask(rmsClusterConfigEntity);
    }

    /**
     * 根据集群id和appId获取running的task
     *
     * @param clusterId
     * @param appId
     * @return
     */
    private List<TaskInfoDto> getRunningTasksByClusterIdAppId(Long clusterId, String appId) {
        TaskDto taskDto = doGetRunningTask(clusterId);
        List<TaskInfoDto> tasks = taskDto.getTasks();
        if (isEmpty(tasks)) {
            return null;
        }

        List<TaskInfoDto> taskInfos = tasks.stream().filter(r -> eq(r.getAppId(), appId)).collect(toList());
        return isEmpty(taskInfos) ? null : taskInfos;
    }

    /**
     * @create: zhangyingbin 2019/11/8 0008 下午 2:29
     * @Modifier:
     * @Description: 创建单节点集群
     */
    @Transactional
    public void createSingleNodeCluster(CreateClusterReq createClusterReq) {

        long id = this.createClusterInfo(createClusterReq);
        this.createClusterConfig(createClusterReq, id);

        if (!isEmpty(createClusterReq.getNodeList())) {
            List<SingleNodeReq> list = createClusterReq.getNodeList();
            for (SingleNodeReq node : list) {
                RmsClusterNodeEntity rmsClusterNodeEntity = new RmsClusterNodeEntity();
                rmsClusterNodeEntity.setClusterId(id);
                rmsClusterNodeEntity.setIp(node.getNodeIp());
                rmsClusterNodeEntity.setCpu(node.getCpu());
                rmsClusterNodeEntity.setMemory(node.getMemory());
                rmsClusterNodeEntity.setCredentialId(node.getCredentialId());
                rmsClusterNodeMapper.insertSelective(rmsClusterNodeEntity);
            }
        }
    }

    /**
     * @create: zhangyingbin 2019/11/8 0008 下午 5:17
     * @Modifier:
     * @Description:
     */
    @Transactional
    public void createMesosCluster(CreateClusterReq createClusterReq) {

        //Get Version
        DcosApi dcos = dcosServerBean.getInstance(PROTOCOL + createClusterReq.getMasterUrls().get(0));
        createClusterReq.setVersion(dcos.getInfo().getVersion());

        long id = this.createClusterInfo(createClusterReq);
        this.createClusterConfig(createClusterReq, id);
    }

    /**
     * @create: zhangyingbin 2019/11/8 0008 下午 6:16
     * @Modifier:
     * @Description: 创建集群基本信息
     */
    private long createClusterInfo(CreateClusterReq createClusterReq) {
        RmsClusterEntity rmsClusterEntity = new RmsClusterEntity();
        rmsClusterEntity.setName(createClusterReq.getName());
        rmsClusterEntity.setType(createClusterReq.getType());
        rmsClusterEntity.setStatus(NORMAL.getCode());

        if (null == createClusterReq.getVersion()) {
            rmsClusterEntity.setVersion("1.0.0");
        } else {
            rmsClusterEntity.setVersion(createClusterReq.getVersion());
        }

        if (null == createClusterReq.getRoomType()) {
            rmsClusterEntity.setRoomType("本地机房");
        }

        if (ClusterTypeEnum.SINGLENODE.getCode().equals(createClusterReq.getType())) {
            BigDecimal cpu = new BigDecimal(0);
            BigDecimal memory = new BigDecimal(0);
            for (SingleNodeReq singleNode : createClusterReq.getNodeList()) {
                cpu = cpu.add(singleNode.getCpu());
                memory = memory.add(singleNode.getMemory());
            }
            rmsClusterEntity.setCpu(cpu);
            rmsClusterEntity.setMemory(memory);
            rmsClusterEntity.setDisk(new BigDecimal(0));
        }

        rmsClusterMapper.insertSelective(rmsClusterEntity);
        return rmsClusterEntity.getId();
    }

    /**
     * @create: zhangyingbin 2019/11/8 0008 下午 6:16
     * @Modifier:
     * @Description: 创建集群配置信息
     */
    private void createClusterConfig(CreateClusterReq createClusterReq, long id) {

        //镜像库
        RmsClusterConfigEntity rmsClusterConfigImage = new RmsClusterConfigEntity();
        rmsClusterConfigImage.setClusterId(id);
        rmsClusterConfigImage.setType("2");
        rmsClusterConfigImage.setUrl(createClusterReq.getImageUrl());
        rmsClusterConfigImage.setCredentialId(createClusterReq.getCredentialId());
        rmsClusterConfigImage.setStatus(NORMAL.getCode());
        rmsClusterConfigMapper.insertSelective(rmsClusterConfigImage);

        //master
        List<String> masterUrls = createClusterReq.getMasterUrls();
        if (null != masterUrls && masterUrls.size() > 0) {
            for (String url : masterUrls) {
                RmsClusterConfigEntity rmsClusterConfigEntity = new RmsClusterConfigEntity();
                rmsClusterConfigEntity.setClusterId(id);
                rmsClusterConfigEntity.setType("0");
                rmsClusterConfigEntity.setUrl(url);
                rmsClusterConfigEntity.setStatus(NORMAL.getCode());
                rmsClusterConfigMapper.insertSelective(rmsClusterConfigEntity);
            }
        }

        //mlb
        List<String> mlbUrls = createClusterReq.getMlbUrls();
        if (null != mlbUrls && mlbUrls.size() > 0) {
            for (String url : mlbUrls) {
                RmsClusterConfigEntity rmsClusterConfigEntity = new RmsClusterConfigEntity();
                rmsClusterConfigEntity.setClusterId(id);
                rmsClusterConfigEntity.setType("1");
                rmsClusterConfigEntity.setUrl(url);
                rmsClusterConfigEntity.setStatus(NORMAL.getCode());
                rmsClusterConfigMapper.insertSelective(rmsClusterConfigEntity);
            }
        }
    }

    /**
     * @return
     * @create: zhangyingbin 2019/11/11 0011 下午 5:34
     * @Modifier:
     * @Description: 根据id获取集群基本信息
     */
    @Override
    public RmsClusterEntity getClusterInfo(Long id) {
        return rmsClusterMapper.selectByPrimaryKey(id);
    }
}
