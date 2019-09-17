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
package net.atayun.bazooka.rms.biz.component.strategy.cluster;

import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.dcos.dto.MesosTaskDto;
import net.atayun.bazooka.base.dcos.dto.TaskDto;
import net.atayun.bazooka.rms.biz.component.bridge.cluster.ClusterStatusCalcBridge;
import net.atayun.bazooka.rms.biz.dal.dao.RmsClusterMapper;
import net.atayun.bazooka.rms.biz.dal.entity.RmsClusterConfigEntity;
import net.atayun.bazooka.rms.biz.dal.entity.RmsClusterEntity;
import net.atayun.bazooka.rms.biz.dal.entity.RmsClusterNodeEntity;
import net.atayun.bazooka.rms.biz.service.RmsClusterAppService;
import net.atayun.bazooka.rms.biz.service.RmsClusterNodeService;
import lombok.extern.slf4j.Slf4j;
import net.atayun.bazooka.rms.biz.enums.ClusterConfigTypeEnum;
import net.atayun.bazooka.rms.biz.enums.ClusterStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.alibaba.fastjson.JSON.parseObject;
import static net.atayun.bazooka.base.bean.StrategyNumBean.getBeanInstance;
import static net.atayun.bazooka.base.constant.CommonConstants.PROTOCOL;
import static net.atayun.bazooka.base.dcos.DcosServerBean.MESOS_TASKS_SUFFIX;
import static net.atayun.bazooka.base.dcos.DcosServerBean.TASKS_SUFFIX;
import static net.atayun.bazooka.base.utils.StringUtil.eq;
import static java.math.BigDecimal.ZERO;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;
import static org.apache.commons.lang3.StringUtils.join;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work Dcos集群组件刷新策略
 */
@Slf4j
@Component
@StrategyNum(superClass = ClusterComponentRefreshStrategy.class, number = "0", describe = "Dcos集群组件刷新策略")
public class ClusterComponentRefreshStrategy4Dcos implements ClusterComponentRefreshStrategy {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RmsClusterNodeService rmsClusterNodeService;
    @Autowired
    private RmsClusterAppService rmsClusterAppService;

    @Autowired
    private RmsClusterMapper rmsClusterMapper;

    /**
     * 执行刷新集群信息
     *
     * @param rmsClusterEntity
     * @param rmsClusterConfigEntities
     */
    @Override
    public void refreshClusterInfo(RmsClusterEntity rmsClusterEntity, List<RmsClusterConfigEntity> rmsClusterConfigEntities) {
        refreshClusterStatus(rmsClusterEntity, rmsClusterConfigEntities);

        refreshClusterApplicationInfo(rmsClusterEntity, rmsClusterConfigEntities);

        refreshCluster(rmsClusterEntity);
    }

    @Override
    public TaskDto getTask(RmsClusterConfigEntity rmsClusterConfigEntity) {
        try {
            String url = join(PROTOCOL, rmsClusterConfigEntity.getUrl(), TASKS_SUFFIX);
            String taskJson = restTemplate.getForObject(url, String.class);
            TaskDto task = parseObject(taskJson, TaskDto.class);
            return task;
        } catch (Exception ex) {
            log.error("获取dcos容器task异常信息:[{}]", getFullStackTrace(ex));
            return null;
        }
    }

    @Override
    public MesosTaskDto getMesosTask(RmsClusterConfigEntity rmsClusterConfigEntity) {
        try {
            String url = join(PROTOCOL, rmsClusterConfigEntity.getUrl(), MESOS_TASKS_SUFFIX);
            String mesosTaskJson = restTemplate.getForObject(url, String.class);
            MesosTaskDto mesosTask = parseObject(mesosTaskJson, MesosTaskDto.class);
            return mesosTask;
        } catch (Exception ex) {
            log.error("获取dcos容器mesos task异常信息:[{}]", getFullStackTrace(ex));
            return null;
        }
    }

    /**
     * 刷新集群状态
     *
     * @param rmsClusterEntity
     * @param rmsClusterConfigEntities
     */
    private void refreshClusterStatus(RmsClusterEntity rmsClusterEntity, List<RmsClusterConfigEntity> rmsClusterConfigEntities) {
        ClusterStatusCalcBridge clusterStatusCalcBridge = getBeanInstance(ClusterStatusCalcBridge.class, rmsClusterEntity.getType());
        String clusterStatus = clusterStatusCalcBridge.calcClusterStatus(rmsClusterConfigEntities);
        rmsClusterEntity.setStatus(clusterStatus);
    }

    /**
     * 刷新集群应用Node,App,Task信息
     *
     * @param rmsClusterEntity
     * @param rmsClusterConfigEntities
     */
    private void refreshClusterApplicationInfo(RmsClusterEntity rmsClusterEntity, List<RmsClusterConfigEntity> rmsClusterConfigEntities) {
        Optional<RmsClusterConfigEntity> validMarathonClusterConfigOptional = rmsClusterConfigEntities.stream().filter(r -> eq(ClusterConfigTypeEnum.MARATHON.getCode(), r.getType()) && eq(ClusterStatusEnum.NORMAL.getCode(), r.getStatus())).findFirst();
        if (validMarathonClusterConfigOptional.isPresent()) {
            RmsClusterConfigEntity validMarathonClusterConfig = validMarathonClusterConfigOptional.get();

            refreshClusterNodeInfo(rmsClusterEntity, validMarathonClusterConfig);
            refreshClusterAppInfo(validMarathonClusterConfig);
        }
    }

    /**
     * 刷新集群节点信息,同时设置集群cpu，内存和磁盘信息
     *
     * @param rmsClusterEntity
     * @param rmsClusterConfigEntity
     */
    private void refreshClusterNodeInfo(RmsClusterEntity rmsClusterEntity, RmsClusterConfigEntity rmsClusterConfigEntity) {
        List<RmsClusterNodeEntity> rmsClusterNodeEntities = rmsClusterNodeService.refreshClusterNodeInfo(rmsClusterConfigEntity);
        refreshClusterCpuMemoryDisk(rmsClusterEntity, rmsClusterNodeEntities);
    }

    /**
     * 刷新集群服务app信息
     *
     * @param rmsClusterConfigEntity
     */
    private void refreshClusterAppInfo(RmsClusterConfigEntity rmsClusterConfigEntity) {
        rmsClusterAppService.refreshClusterAppInfo(rmsClusterConfigEntity);
    }

    /**
     * 刷新集群cpu，内存和磁盘信息
     *
     * @param rmsClusterEntity
     * @param rmsClusterNodeEntities
     */
    private void refreshClusterCpuMemoryDisk(RmsClusterEntity rmsClusterEntity, List<RmsClusterNodeEntity> rmsClusterNodeEntities) {
        if (isEmpty(rmsClusterNodeEntities)) {
            return;
        }
        rmsClusterEntity.setCpu(rmsClusterNodeEntities.stream().map(r -> r.getCpu()).reduce(ZERO, BigDecimal::add));
        rmsClusterEntity.setMemory(rmsClusterNodeEntities.stream().map(r -> r.getMemory()).reduce(ZERO, BigDecimal::add));
        rmsClusterEntity.setDisk(rmsClusterNodeEntities.stream().map(r -> r.getDisk()).reduce(ZERO, BigDecimal::add));
    }

    /**
     * 刷新集群
     *
     * @param rmsClusterEntity
     */
    private void refreshCluster(RmsClusterEntity rmsClusterEntity) {
        rmsClusterMapper.updateByPrimaryKeySelective(rmsClusterEntity);
    }
}
