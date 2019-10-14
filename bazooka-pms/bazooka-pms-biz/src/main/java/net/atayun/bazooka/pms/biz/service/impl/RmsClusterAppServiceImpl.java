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

import net.atayun.bazooka.combase.dcos.dto.AppDto;
import net.atayun.bazooka.combase.dcos.dto.AppInfoDto;
import net.atayun.bazooka.combase.service.BatchService;
import net.atayun.bazooka.combase.tuple.Tuple2;
import net.atayun.bazooka.combase.utils.JsonUtil;
import net.atayun.bazooka.pms.biz.dal.dao.RmsClusterAppMapper;
import net.atayun.bazooka.pms.biz.dal.entity.RmsClusterAppEntity;
import net.atayun.bazooka.pms.biz.dal.entity.RmsClusterConfigEntity;
import net.atayun.bazooka.pms.biz.service.RmsClusterAppService;
import net.atayun.bazooka.pms.biz.service.RmsClusterConfigService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.youyu.common.service.AbstractService;
import lombok.extern.slf4j.Slf4j;
import net.atayun.bazooka.pms.api.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.alibaba.fastjson.JSON.parseObject;
import static net.atayun.bazooka.combase.constant.CommonConstants.PROTOCOL;
import static net.atayun.bazooka.combase.dcos.DcosServerBean.APPS_SUFFIX;
import static net.atayun.bazooka.combase.utils.OrikaCopyUtil.copyProperty;
import static net.atayun.bazooka.combase.utils.StringUtil.eq;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static net.atayun.bazooka.pms.api.enums.ClusterAppServiceStatusEnum.*;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;
import static org.apache.commons.lang3.StringUtils.*;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 资源集群App service impl
 */
@Slf4j
@Service
public class RmsClusterAppServiceImpl extends AbstractService<Long, RmsClusterAppDto, RmsClusterAppEntity, RmsClusterAppMapper> implements RmsClusterAppService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private BatchService batchService;

    @Autowired
    private RmsClusterConfigService rmsClusterConfigService;

    @Autowired
    private RmsClusterAppMapper rmsClusterAppMapper;

    private final Gson gson = JsonUtil.GSON;

    @Override
    public void refreshClusterAppInfo(RmsClusterConfigEntity rmsClusterConfigEntity) {
        try {
            String url = join(PROTOCOL, rmsClusterConfigEntity.getUrl(), APPS_SUFFIX);
            AppDto app = getApp(url);
            doRefreshClusterAppInfo(rmsClusterConfigEntity, app);
        } catch (Exception ex) {
            log.error("刷新集群地址:[{}]对应app异常信息:[{}]", rmsClusterConfigEntity.getUrl(), getFullStackTrace(ex));
        }
    }

    @Override
    public ClusterAppResourceDto getClusterAppResource(String appId, Long clusterId) {
        appId = protectAppId(appId);
        ClusterAppResourceDto clusterAppResource = rmsClusterAppMapper.getClusterAppResourceByClusterIdAppIdActive(clusterId, appId, true);
        return clusterAppResource;
    }

    @Override
    public ClusterAppServiceInfoDto getClusterAppServiceInfo(Long clusterId, String appId) {
        appId = protectAppId(appId);
        RmsClusterAppEntity rmsClusterAppEntity = rmsClusterAppMapper.getClusterAppByClusterIdAppIdActive(clusterId, appId, true);
        if (isNull(rmsClusterAppEntity)) {
            ClusterAppServiceInfoDto clusterAppServiceInfo = new ClusterAppServiceInfoDto();
            clusterAppServiceInfo.setStatus(UNPUBLISHED.getCode());
            return clusterAppServiceInfo;
        }

        ClusterAppServiceInfoDto clusterAppServiceInfo = copyProperty(rmsClusterAppEntity, ClusterAppServiceInfoDto.class);
        clusterAppServiceInfo.setStatus(getClusterAppServiceStatus(rmsClusterAppEntity));
        return clusterAppServiceInfo;
    }

    @Override
    public List<ClusterAppServiceHostDto> getClusterAppServiceHosts(Long clusterId, String appId) {
        ClusterConfigDto clusterConfig = rmsClusterConfigService.getClusterConfig(clusterId);
        if (isNull(clusterConfig)) {
            return null;
        }

        appId = protectAppId(appId);
        RmsClusterAppEntity rmsClusterAppEntity = rmsClusterAppMapper.getClusterAppByClusterIdAppIdActive(clusterId, appId, true);
        if (isNull(rmsClusterAppEntity)) {
            return null;
        }

        return getClusterAppServiceHosts(clusterConfig, rmsClusterAppEntity);
    }

    @Override
    public String getClusterAppImage(Long clusterId, String appId) {
        RmsClusterAppEntity rmsClusterAppEntity = rmsClusterAppMapper.getClusterAppByClusterIdAppIdActive(clusterId, appId, true);
        if (isNull(rmsClusterAppEntity)) {
            return null;
        }

        return rmsClusterAppEntity.getImage();
    }

    /**
     * 获取集群app服务地址信息
     * <p>
     * "labels": {
     * "HAPROXY_GROUP": "external",					#如果发布配置中至少有一个端口需要“提供集群外访问”
     * "HAPROXY_1_FRONTEND_HEAD": "",					#如果端口0（此例子中为8080）需要集群外访问，而端口1（此例子中为9999）不需要，增加这一行。数字1是端口编号
     * "HAPROXY_1_BACKEND_HEAD": "",					#如果端口0（此例子中为8080）需要集群外访问，而端口1（此例子中为9999）不需要，增加这一行。数字1是端口编号
     * "HAPROXY_1_FRONTEND_BACKEND_GLUE": "",			#如果端口0（此例子中为8080）需要集群外访问，而端口1（此例子中为9999）不需要，增加这一行。数字1是端口编号
     * },
     *
     * @param clusterConfig
     * @param rmsClusterAppEntity
     * @return
     */
    private List<ClusterAppServiceHostDto> getClusterAppServiceHosts(ClusterConfigDto clusterConfig, RmsClusterAppEntity rmsClusterAppEntity) {
        List<String> mlbUrls = clusterConfig.getMlbUrls();
        List<String> servicePorts = Stream.of(split(rmsClusterAppEntity.getServicePort(), ",")).collect(toList());
        List<String> containerPorts = Stream.of(split(rmsClusterAppEntity.getContainerPort(), ",")).collect(toList());

        //判断是否包含 HAPROXY_GROUP 配置，如果没有则不会映射servicePort
        boolean hasHaproxyGroup = false;
        JsonObject labels = this.getAppConfigLabels(rmsClusterAppEntity.getAppJson());

        if (!ObjectUtils.isEmpty(labels)) {
            hasHaproxyGroup = labels.has("HAPROXY_GROUP");
        }

        List<ClusterAppServiceHostDto> clusterAppServiceHosts = new ArrayList<>();
        for (int i = 0; i < containerPorts.size(); i++) {
            ClusterAppServiceHostDto clusterAppServiceHost = new ClusterAppServiceHostDto();
            clusterAppServiceHost.setContainerPort(containerPorts.get(i));
            if (hasHaproxyGroup && this.checkServicePortIsEnabled(labels, i)) {
                List<String> mlbHosts = new ArrayList<>();
                for (String mlbUrl : mlbUrls) {
                    mlbHosts.add(join(mlbUrl, ":", servicePorts.get(i)));
                }
                clusterAppServiceHost.setMlbHosts(mlbHosts);
            }
            clusterAppServiceHosts.add(clusterAppServiceHost);
        }

        return clusterAppServiceHosts;
    }

    private JsonObject getAppConfigLabels(String appJson) {
        JsonObject labels = null;
        if (StringUtils.hasText(appJson)) {
            try {
                JsonObject jsonObject = gson.fromJson(appJson, JsonObject.class);
                labels = jsonObject.getAsJsonObject("labels");
            } catch (Exception e) {
                log.warn("解析appJson异常");
            }
        }
        return labels;
    }

    private boolean checkServicePortIsEnabled(JsonObject lables, int portIndex) {
        if (ObjectUtils.isEmpty(lables)) {
            return false;
        }
        boolean has = lables.has(MessageFormat.format("HAPROXY_{0}_FRONTEND_BACKEND_GLUE", portIndex));
        return has ? false : true;
    }

    /**
     * 保护AppId,增加前缀/
     *
     * @param appId
     * @return
     */
    private String protectAppId(String appId) {
        if (!startsWith(appId, "/")) {
            appId = "/" + appId;
        }
        return appId;
    }

    /**
     * 获取集群app服务的状态
     *
     * @param rmsClusterAppEntity
     * @return
     */
    private String getClusterAppServiceStatus(RmsClusterAppEntity rmsClusterAppEntity) {
        Integer tasksStaged = rmsClusterAppEntity.getTasksStaged();
        Integer tasksRunning = rmsClusterAppEntity.getTasksRunning();
        if (tasksStaged == 0 && tasksRunning == 0) {
            return CLOSED.getCode();
        }

        if (tasksStaged == 0 && tasksRunning > 0) {
            return RUNNING.getCode();
        }

        return STARTING.getCode();
    }

    /**
     * 获取app信息
     *
     * @param url
     */
    private AppDto getApp(String url) {
        String appJson = restTemplate.getForObject(url, String.class);
        AppDto app = parseObject(appJson, AppDto.class);
        return app;
    }

    /**
     * 执行刷新集群app信息
     *
     * @param rmsClusterConfigEntity
     * @param app
     */
    private void doRefreshClusterAppInfo(RmsClusterConfigEntity rmsClusterConfigEntity, AppDto app) {
        List<String> appJsons = app.getApps();
        if (isEmpty(appJsons)) {
            return;
        }

        List<RmsClusterAppEntity> rmsClusterAppEntities = new ArrayList<>();
        for (String appJson : appJsons) {
            AppInfoDto appInfo = parseObject(appJson, AppInfoDto.class);
            try {
                RmsClusterAppEntity rmsClusterAppEntity = new RmsClusterAppEntity(rmsClusterConfigEntity, appJson, appInfo);
                rmsClusterAppEntities.add(rmsClusterAppEntity);
            } catch (Exception ex) {
                log.error("解析appJson:[{}]对应的异常信息:[{}]", appJson, getFullStackTrace(ex));
                continue;
            }
        }

        Tuple2<List<RmsClusterAppEntity>, List<RmsClusterAppEntity>> tuple2 = getInsertUpdate(rmsClusterAppEntities, rmsClusterConfigEntity.getClusterId());
        rmsClusterAppMapper.updateAllInactiveByClusterId(rmsClusterConfigEntity.getClusterId());
        doInsertClusterApp(tuple2.a);
        doUpdateClusterApp(tuple2.b);
    }

    private void doUpdateClusterApp(List<RmsClusterAppEntity> rmsClusterAppEntities) {
        if (isEmpty(rmsClusterAppEntities)) {
            return;
        }

//        List<String> appIds = rmsClusterAppEntities.stream().map(r -> r.getAppId()).collect(toList());
//        batchService.batchDispose(appIds, RmsClusterAppMapper.class, "updateInactiveByAppId");
        batchService.batchDispose(rmsClusterAppEntities, RmsClusterAppMapper.class, "updateByPrimaryKeySelective");
    }

    private void doInsertClusterApp(List<RmsClusterAppEntity> rmsClusterAppEntities) {
        if (isEmpty(rmsClusterAppEntities)) {
            return;
        }

//        List<String> appIds = rmsClusterAppEntities.stream().map(r -> r.getAppId()).collect(toList());
//        batchService.batchDispose(appIds, RmsClusterAppMapper.class, "updateInactiveByAppId");
        batchService.batchDispose(rmsClusterAppEntities, RmsClusterAppMapper.class, "insertSelective");
    }

    /**
     * 获取新增和更新的二元组
     *
     * @param rmsClusterAppEntities
     * @param clusterId
     * @return
     */
    private Tuple2<List<RmsClusterAppEntity>, List<RmsClusterAppEntity>> getInsertUpdate(List<RmsClusterAppEntity> rmsClusterAppEntities, Long clusterId) {
        List<RmsClusterAppEntity> insertList = new ArrayList<>();
        List<RmsClusterAppEntity> updateList = new ArrayList<>();
        RmsClusterAppEntity queryRmsClusterAppEntity = new RmsClusterAppEntity();
        queryRmsClusterAppEntity.setClusterId(clusterId);
        List<RmsClusterAppEntity> rmsClusterApps = rmsClusterAppMapper.select(queryRmsClusterAppEntity);
        for (RmsClusterAppEntity rmsClusterAppEntity : rmsClusterAppEntities) {
            Optional<RmsClusterAppEntity> rmsClusterAppEntityOptional = rmsClusterApps.stream().filter(r -> eq(r.getAppId(), rmsClusterAppEntity.getAppId()) && eq(r.getVersion(), rmsClusterAppEntity.getVersion())).findFirst();
            if (rmsClusterAppEntityOptional.isPresent()) {
                rmsClusterAppEntity.setId(rmsClusterAppEntityOptional.get().getId());
                updateList.add(rmsClusterAppEntity);
            } else {
                insertList.add(rmsClusterAppEntity);
            }
        }
        return new Tuple2<>(insertList, updateList);
    }

}




