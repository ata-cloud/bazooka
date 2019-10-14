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

import net.atayun.bazooka.combase.page.PageQuery;
import net.atayun.bazooka.pms.api.dto.PmsUserProjectRelationDto;
import net.atayun.bazooka.pms.api.dto.ProjectInfoDto;
import net.atayun.bazooka.pms.api.enums.UserTypeEnum;
import net.atayun.bazooka.pms.api.vo.DevUserResponse;
import net.atayun.bazooka.pms.biz.dal.dao.PmsUserProjectRelationMapper;
import net.atayun.bazooka.pms.biz.dal.entity.PmsGitlabUserEntity;
import net.atayun.bazooka.pms.biz.dal.entity.PmsUserProjectRelationEntity;
import net.atayun.bazooka.pms.biz.service.PmsGitlabUserService;
import net.atayun.bazooka.pms.biz.service.PmsProjectInfoService;
import net.atayun.bazooka.pms.biz.service.PmsUserProjectRelationService;
import net.atayun.bazooka.pms.biz.service.ProjectExtra;
import com.github.pagehelper.PageInfo;
import com.youyu.common.api.PageData;
import com.youyu.common.service.AbstractService;
import com.youyu.common.transfer.BaseBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static net.atayun.bazooka.combase.utils.PageDataUtil.pageInfo2PageData;
import static com.github.pagehelper.page.PageMethod.startPage;

/**
 * @author rache
 * @date 2019-07-19
 */
@Service
        public class PmsUserProjectRelationServiceImpl extends AbstractService<Long, PmsUserProjectRelationDto, PmsUserProjectRelationEntity, PmsUserProjectRelationMapper> implements PmsUserProjectRelationService   {

    @Autowired
    private PmsGitlabUserService pmsGitlabUserService;

    @Autowired
    private ProjectExtra projectExtraGitlab;
    @Autowired
    private PmsProjectInfoService pmsProjectInfoService;
    @Override
    public List<PmsUserProjectRelationEntity> queryUserForProject(Long projectId, UserTypeEnum roleType) {
        PmsUserProjectRelationEntity entity=new PmsUserProjectRelationEntity();
        entity.setProjectId(projectId);
        entity.setRoleType(roleType);
        List<PmsUserProjectRelationEntity> entities= super.selectEntity(entity);
        return entities;
    }

    @Override
    public PageData<DevUserResponse> queryUserForProject(PageQuery pageQuery, Long projectId, UserTypeEnum roleType) {

        startPage(pageQuery.getPageNo(), pageQuery.getPageSize());
        List<PmsUserProjectRelationEntity> users = queryUserForProject(projectId, roleType);
        PageInfo userPage = new PageInfo(users);

        List<DevUserResponse> userQueryRsps = BaseBeanUtils.copy(users, DevUserResponse.class);
        return pageInfo2PageData(userPage, userQueryRsps);
    }

    @Override
    public List<PmsUserProjectRelationEntity> queryProjectByUser(Long userId){
        PmsUserProjectRelationEntity entity=new PmsUserProjectRelationEntity();
        entity.setUserId(userId);
        List<PmsUserProjectRelationEntity> entities= super.selectEntity(entity);
        return entities;
    }

    @Override
    public PmsUserProjectRelationEntity queryProjectMaster(Long projectId) {
        List<PmsUserProjectRelationEntity> relations= queryUserForProject(projectId,UserTypeEnum.USER_PROJECT_MASTER);
        if(relations==null||relations.size()==0){
            return null;
        }
        PmsUserProjectRelationEntity relationEntity= relations.stream().findFirst().get();
        return relationEntity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUserForProject(Long userId, Long projectId, UserTypeEnum roleType) {
        PmsGitlabUserEntity gitlabUser= pmsGitlabUserService.getUserInfo(userId);
        ProjectInfoDto pjd= pmsProjectInfoService.queryProjectById(projectId);
        addUserForProject(userId, projectId, roleType,pjd.getGitlabGroupId(),gitlabUser.getGitlabUserId());
    }

    /**
     * 添加用用户项目关系简化数据库操作
     * @param userId
     * @param projectId
     * @param roleType
     * @param gitlabGroupId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUserForProject(Long userId, Long projectId, UserTypeEnum roleType,Integer gitlabGroupId) {
        PmsGitlabUserEntity gitlabUser= pmsGitlabUserService.getUserInfo(userId);
        addUserForProject(userId, projectId, roleType,gitlabGroupId,gitlabUser.getGitlabUserId());
    }

    /**
     * 添加用用户项目关系简化数据库操作
     * @param userId
     * @param projectId
     * @param roleType
     * @param gitlabGroupId
     * @param gitLabUserId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUserForProject(Long userId, Long projectId, UserTypeEnum roleType
            ,Integer gitlabGroupId,Integer gitLabUserId) {
        addUserForProjectDb(userId, projectId, roleType);
        projectExtraGitlab.addProjectMember(gitlabGroupId,gitLabUserId,roleType);
    }
    private void addUserForProjectDb(Long userId, Long projectId, UserTypeEnum roleType) {
        PmsUserProjectRelationEntity entity=new PmsUserProjectRelationEntity();
        entity.setUserId(userId);
        entity.setProjectId(projectId);
        entity.setRoleType(roleType);
        super.insertSelective(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserForProject(Long userId, Long projectId, UserTypeEnum roleType) {
        ProjectInfoDto pjd= pmsProjectInfoService.queryProjectById(projectId);
        PmsGitlabUserEntity gitlabUser= pmsGitlabUserService.getUserInfo(userId);
        deleteUserForProject(userId, projectId, roleType,pjd.getGitlabGroupId(),gitlabUser.getGitlabUserId());
    }

    @Override
    public void deleteUserForProject(Long userId, Long projectId, UserTypeEnum roleType, Integer groupId) {
        PmsGitlabUserEntity gitlabUser= pmsGitlabUserService.getUserInfo(userId);
        deleteUserForProject(userId, projectId, roleType,groupId,gitlabUser.getGitlabUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserForProject(Long userId, Long projectId, UserTypeEnum roleType,Integer gitlabGroupId,
                                     Integer gitlabUserId) {
        int resultCount= deleteUserForProjectDb(userId, projectId, roleType);
        if(resultCount==0){
            return;
        }
        projectExtraGitlab.deleteProjectMember(gitlabGroupId,gitlabUserId);
    }

    @Override
    public void UpdateUserForProject(Long userId, Long projectId, UserTypeEnum roleType, Integer gitlabGroupI) {
        PmsGitlabUserEntity gitlabUser= pmsGitlabUserService.getUserInfo(userId);
        UpdateUserForProject(userId, projectId, roleType, gitlabGroupI,gitlabUser.getGitlabUserId());
    }

    @Override
    public void UpdateUserForProject(Long userId, Long projectId, UserTypeEnum roleType, Integer gitlabGroupId, Integer gitLabUserId) {
        UpdateUserForProjectDb(userId, projectId, roleType);
        projectExtraGitlab.updateProjectMember(gitlabGroupId,gitLabUserId,roleType);
    }

    private void UpdateUserForProjectDb(Long userId, Long projectId, UserTypeEnum roleType) {

        super.mapper.updateRelation(projectId,userId,roleType);
    }



    @Override
    public void changeUserForProjectSameRole(Long oldUserId, Long newUserId, Long projectId, UserTypeEnum roleType,Integer groupId) {
        //移除老的用户
        deleteUserForProject(oldUserId,projectId,roleType,groupId);
        if(checkIsInProject(projectId,newUserId)){
            UpdateUserForProject(newUserId,projectId,roleType,groupId);
        }else {
            addUserForProject(newUserId,projectId,roleType,groupId);
        }

    }

    @Override
    public List<Long> queryEnvForUser(Long userId) {
       return super.mapper.queryUserEnvInfo(userId);
    }

    public boolean checkIsInProject(Long projectId,Long userId){
        PmsUserProjectRelationEntity entity=new PmsUserProjectRelationEntity();
        entity.setUserId(userId);
        entity.setProjectId(projectId);
        List<PmsUserProjectRelationEntity> userRelation= super.selectEntity(entity);
        if(userRelation!=null&&userRelation.size()>0){
            return true;
        }
        return false;
    }

    private Integer deleteUserForProjectDb(Long userId, Long projectId, UserTypeEnum roleType){
        PmsUserProjectRelationDto entity=new PmsUserProjectRelationDto();
        entity.setUserId(userId);
        entity.setProjectId(projectId);
        entity.setRoleType(roleType);
        return super.delete(entity);
    }
}
