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


import net.atayun.bazooka.pms.api.dto.PmsProjectEnvRelationDto;
import net.atayun.bazooka.pms.biz.dal.entity.PmsProjectEnvRelationEntity;
import com.youyu.common.service.IService;

import java.util.List;

/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2019-07-26
 */
public interface PmsProjectEnvRelationService extends IService<PmsProjectEnvRelationDto, PmsProjectEnvRelationEntity> {

    List<Long> queryEnvListForProject(Long projectId);

    /**
     *
     * @param envId
     * @return
     */
    List<PmsProjectEnvRelationEntity> selectProjectNotDelete(Long envId);

}




