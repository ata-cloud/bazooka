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
package net.atayun.bazooka.pms.biz.component.strategy.cluster;

import net.atayun.bazooka.combase.annotation.StrategyNum;
import net.atayun.bazooka.pms.biz.dal.dao.RmsClusterConfigMapper;
import net.atayun.bazooka.pms.biz.dal.entity.RmsClusterConfigEntity;
import net.atayun.bazooka.pms.biz.enums.ClusterStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static net.atayun.bazooka.combase.constant.CommonConstants.PROTOCOL;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;
import static org.apache.commons.lang3.StringUtils.join;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 集群Marathon状态计算策略
 */
@Slf4j
@Component
@StrategyNum(superClass = ClusterComponentStatusCalcStrategy.class, number = "0", describe = "集群Marathon状态计算策略")
public class ClusterComponentStatusCalcStrategy4Marathon implements ClusterComponentStatusCalcStrategy {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RmsClusterConfigMapper rmsClusterConfigMapper;

    @Override
    public void doCalcComponentStatus(RmsClusterConfigEntity rmsClusterConfigEntity) {
        String url = join(PROTOCOL, rmsClusterConfigEntity.getUrl(), ":8080/ping");
        String marathonStatus = getMarathonStatus(url);
        rmsClusterConfigEntity.setStatus(marathonStatus);
        rmsClusterConfigMapper.updateByPrimaryKeySelective(rmsClusterConfigEntity);
    }

    /**
     * 获取Marathon状态
     *
     * @param url
     * @return
     */
    private String getMarathonStatus(String url) {
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            return responseEntity.getStatusCode().is2xxSuccessful() ? ClusterStatusEnum.NORMAL.getCode() : ClusterStatusEnum.ABNORMAL.getCode();
        } catch (Exception ex) {
            log.error("获取地址:[{}]对应dcos异常信息:[{}]", url, getFullStackTrace(ex));
            return ClusterStatusEnum.ABNORMAL.getCode();
        }
    }
}
