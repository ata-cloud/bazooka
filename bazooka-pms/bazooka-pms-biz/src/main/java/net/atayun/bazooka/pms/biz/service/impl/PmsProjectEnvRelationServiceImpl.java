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


import net.atayun.bazooka.pms.api.dto.PmsProjectEnvRelationDto;
import net.atayun.bazooka.pms.biz.dal.dao.PmsProjectEnvRelationMapper;
import net.atayun.bazooka.pms.biz.dal.entity.PmsProjectEnvRelationEntity;
import net.atayun.bazooka.pms.biz.service.PmsProjectEnvRelationService;
import com.youyu.common.service.AbstractService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 *  代码生成器
 *
 * @author 技术平台
 * @date 2019-07-26
 */
@Service
public class PmsProjectEnvRelationServiceImpl extends AbstractService<Long, PmsProjectEnvRelationDto, PmsProjectEnvRelationEntity, PmsProjectEnvRelationMapper> implements PmsProjectEnvRelationService {

    @Override
    public List<Long> queryEnvListForProject(Long projectId) {
        PmsProjectEnvRelationEntity envRelationEntity=new PmsProjectEnvRelationEntity();
        envRelationEntity.setProjectId(projectId);
        List<PmsProjectEnvRelationEntity> list= super.mapper.select(envRelationEntity);
        if(list!=null&&list.size()>0){
            return list.stream().map(m->m.getEnvId()).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<PmsProjectEnvRelationEntity> selectProjectNotDelete(Long envId) {
       return mapper.selectProjectNotDelete(envId);
    }
}




