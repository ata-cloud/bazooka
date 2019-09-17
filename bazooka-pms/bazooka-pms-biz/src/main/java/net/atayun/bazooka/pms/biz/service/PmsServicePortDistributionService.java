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
package net.atayun.bazooka.pms.biz.service;

import net.atayun.bazooka.pms.api.dto.PmsServicePortDistributionDto;
import net.atayun.bazooka.pms.biz.dal.entity.PmsServicePortDistributionEntity;
import com.youyu.common.service.IService;

/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2019-07-31
 */
public interface PmsServicePortDistributionService extends IService<PmsServicePortDistributionDto, PmsServicePortDistributionEntity> {

    /**
     * 分配端口
     *
     * @param projectId
     * @param appId
     * @param envId
     * @param containerPort
     * @return
     */
    Integer distributionServicePort(Long projectId, Long appId, Long envId, Integer containerPort);

    /**
     * 服务端口检查（是否可用）
     *
     * @param projectId
     * @param appId
     * @param envId
     * @param containerPort
     * @param servicePort
     */
    void servicePortCheck(Long projectId, Long appId, Long envId, Integer containerPort, Integer servicePort);

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
    int saveServicePort(Long projectId, Long appId, Long envId, Long configId,Integer containerPort, Integer servicePort);
}




