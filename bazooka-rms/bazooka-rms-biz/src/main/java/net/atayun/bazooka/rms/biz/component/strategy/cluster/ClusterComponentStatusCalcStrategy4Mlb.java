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
import net.atayun.bazooka.rms.biz.dal.dao.RmsClusterConfigMapper;
import net.atayun.bazooka.rms.biz.dal.entity.RmsClusterConfigEntity;
import lombok.extern.slf4j.Slf4j;
import net.atayun.bazooka.rms.biz.enums.ClusterStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;

import static net.atayun.bazooka.base.constant.CommonConstants.PROTOCOL;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 集群Mlb状态计算策略
 */
@Slf4j
@Component
@StrategyNum(superClass = ClusterComponentStatusCalcStrategy.class, number = "1", describe = "集群Mlb状态计算策略")
public class ClusterComponentStatusCalcStrategy4Mlb implements ClusterComponentStatusCalcStrategy {

    /**
     * 后台
     */
    private static final String BACKEND = "BACKEND";
    /**
     * 前台
     */
    private static final String FRONTEND = "FRONTEND";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RmsClusterConfigMapper rmsClusterConfigMapper;

    @Override
    public void doCalcComponentStatus(RmsClusterConfigEntity rmsClusterConfigEntity) {
        String url = join(PROTOCOL, rmsClusterConfigEntity.getUrl(), ":9090/haproxy?stats;csv");
        String mlbStatus = getMlbStatus(url);
        rmsClusterConfigEntity.setStatus(mlbStatus);
        rmsClusterConfigMapper.updateByPrimaryKeySelective(rmsClusterConfigEntity);
    }

    /**
     * 获取Mlb状态
     *
     * @param url
     * @return
     */
    private String getMlbStatus(String url) {
        try {
            List<String> bodyList = asList(split(restTemplate.getForObject(url, String.class), "\n"));
            bodyList = bodyList.stream().filter(s -> !containsAny(s, BACKEND, FRONTEND)).collect(toList());
            Set<String> bodySet = bodyList.stream().map(s -> substringBefore(s, ",")).collect(toSet());
            Set<String> portSet = bodyList.stream().map(s -> {
                s = substringBetween(s, "_", ",");
                return contains(s, "_") ? substringAfterLast(s, "_") : s;
            }).collect(toSet());
            return bodySet.size() == portSet.size() ? ClusterStatusEnum.NORMAL.getCode() : ClusterStatusEnum.USABLE.getCode();
        } catch (Exception e) {
            log.error("获取地址:[{}]对应MarathonLb状态异常信息:[{}]", url, getFullStackTrace(e));
            return ClusterStatusEnum.ABNORMAL.getCode();
        }
    }


}
