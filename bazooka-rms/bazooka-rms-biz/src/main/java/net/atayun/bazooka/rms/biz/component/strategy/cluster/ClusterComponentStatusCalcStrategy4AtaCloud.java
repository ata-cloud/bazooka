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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;
import static org.apache.commons.lang3.StringUtils.join;

/**
 * AtaCloud状态计算策略
 *
 * @author WangSongJun
 * @date 2019-08-26
 */
@Slf4j
@Component
@StrategyNum(superClass = ClusterComponentStatusCalcStrategy.class, number = "5", describe = "AtaCloud状态计算策略")
public class ClusterComponentStatusCalcStrategy4AtaCloud implements ClusterComponentStatusCalcStrategy {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RmsClusterConfigMapper rmsClusterConfigMapper;

    @Override
    public void doCalcComponentStatus(RmsClusterConfigEntity rmsClusterConfigEntity) {
        String url = join(rmsClusterConfigEntity.getUrl());
        String ataCloudStatus = checkAtaCloudStatus(url);
        rmsClusterConfigEntity.setStatus(ataCloudStatus);
        rmsClusterConfigMapper.updateByPrimaryKeySelective(rmsClusterConfigEntity);
    }

    /**
     * 获取AtaCloud状态
     *
     * @param url
     * @return
     */
    private String checkAtaCloudStatus(String url) {
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            return responseEntity.getStatusCode().is2xxSuccessful() ? ClusterStatusEnum.NORMAL.getCode() : ClusterStatusEnum.ABNORMAL.getCode();
        } catch (Exception e) {
            log.error("获取地址:[{}]对应AtaCloud状态异常信息:[{}]", url, getFullStackTrace(e));
            return ClusterStatusEnum.ABNORMAL.getCode();
        }
    }
}
