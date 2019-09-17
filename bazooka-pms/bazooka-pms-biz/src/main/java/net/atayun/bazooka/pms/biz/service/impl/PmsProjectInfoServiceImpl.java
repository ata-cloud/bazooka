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

import net.atayun.bazooka.pms.api.dto.PmsProjectInfoDto;
import net.atayun.bazooka.pms.api.dto.ProjectCountDto;
import net.atayun.bazooka.pms.api.dto.ProjectInfoDto;
import net.atayun.bazooka.pms.biz.dal.dao.PmsProjectInfoMapper;
import net.atayun.bazooka.pms.biz.dal.entity.PmsProjectInfoEntity;
import net.atayun.bazooka.pms.biz.service.PmsProjectInfoService;
import com.youyu.common.enums.IsDeleted;
import com.youyu.common.service.AbstractService;
import com.youyu.common.utils.YyBeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author rache
 * @date 2019-07-25
 */
@Service
public class PmsProjectInfoServiceImpl extends AbstractService<Long, PmsProjectInfoDto, PmsProjectInfoEntity, PmsProjectInfoMapper> implements PmsProjectInfoService {

    /**
     * 根据项目id获取项目信息
     * @param id
     * @return
     */
    @Override
    public ProjectInfoDto queryProjectById(long id) {
        ProjectInfoDto dto = new ProjectInfoDto();
        PmsProjectInfoEntity pmsProjectInfoEntity = super.mapper.selectByPrimaryKey(id);
        if (pmsProjectInfoEntity == null||pmsProjectInfoEntity.getIsDeleted().equals(IsDeleted.DELETED)) {
            return null;
        }
        YyBeanUtils.copyProperties(pmsProjectInfoEntity,dto);
        return dto;
    }

    /**
     * 包含被删除的
     * @param projectName
     * @param projectCode
     * @return
     */
    @Override
    public Integer countProjectInfo(String projectName,String projectCode,Long projectId) {
       return super.mapper.countProjectInfo(projectName, projectCode,projectId);
    }


    @Override
    public List<ProjectCountDto> queryProjectCount(String keyWord){
        return super.mapper.queryProjectCount(keyWord);
    }

    @Override
    public Integer deleteProject(Long projectId){
        PmsProjectInfoEntity entity=new PmsProjectInfoEntity();
        entity.setId(projectId);
        entity.setIsDeleted(IsDeleted.DELETED);
        return super.mapper.updateByPrimaryKeySelective(entity);
    }

    @Override
    public List<PmsProjectInfoDto> queryProjectListForAdmin(Long userId) {
       return super.mapper.queryProjectListForAdmin(userId);
    }
}
