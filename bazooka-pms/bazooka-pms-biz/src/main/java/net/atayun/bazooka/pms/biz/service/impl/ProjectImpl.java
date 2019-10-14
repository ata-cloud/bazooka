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

import net.atayun.bazooka.pms.api.EnvDto;
import net.atayun.bazooka.pms.api.dto.*;
import net.atayun.bazooka.pms.api.enums.*;
import net.atayun.bazooka.pms.api.vo.ProjectRequest;
import net.atayun.bazooka.pms.api.vo.UpdateProjectReq;
import net.atayun.bazooka.pms.biz.constant.PmsConstant;
;
import net.atayun.bazooka.pms.biz.dal.entity.AppInfoEntity;
import net.atayun.bazooka.pms.biz.dal.entity.PmsProjectEnvRelationEntity;
import net.atayun.bazooka.pms.biz.dal.entity.PmsProjectInfoEntity;
import net.atayun.bazooka.pms.biz.dal.entity.PmsUserProjectRelationEntity;
import net.atayun.bazooka.pms.biz.service.*;
import net.atayun.bazooka.pms.api.api.EnvApi;
import net.atayun.bazooka.upms.api.feign.UserApi;
import com.youyu.common.enums.IsDeleted;
import com.youyu.common.exception.BizException;
import com.youyu.common.transfer.BaseBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static net.atayun.bazooka.pms.api.enums.PmsResultCode.*;


/**
 * @author rache
 * @date 2019-07-16
 */
@Slf4j
@Service
public class ProjectImpl implements ProjectService {

    @Autowired
    private PmsProjectEnvRelationService pmsProjectEnvRelationService;

    @Autowired
    private ProjectExtra projectExtra;
    @Autowired
    private PmsUserProjectRelationService pmsUserProjectRelationService;
    @Autowired
    private PmsProjectInfoService pmsProjectInfoService;
    @Autowired
    private UserApi userApi;
    @Autowired
    private EnvApi envApi;
    @Autowired
    private AppService appService;
    @Autowired
    private AppInfoService appInfoService;
    /**
     * 创建项目基础信息
     * @param projectName
     * @param projectCode
     * @param description
     * @return 创建的projectId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PmsProjectInfoEntity createProject(String projectName, String projectCode, String description) {
        checkIsRepeat(projectName, projectCode,0L);
        Integer gitlabGroupId= projectExtra.createProject(projectCode);
        PmsProjectInfoEntity entity = new PmsProjectInfoEntity();
        entity.setProjectName(projectName);
        entity.setProjectCode(projectCode);
        entity.setDescription(description);
        entity.setGitlabGroupId(gitlabGroupId);
        pmsProjectInfoService.insertSelective(entity);
        return entity;
    }

    /**
     * 检查项目创建
     *
     * @param name
     * @param projectCode
     */
    private void checkIsRepeat(String name, String projectCode,Long projectId) {
        Integer count = pmsProjectInfoService.countProjectInfo(name, projectCode,projectId);
        if (count > 0) {
            throw new BizException(PROJECT_SAME_INFO.getCode(), PROJECT_SAME_INFO.getDesc());
        }
    }

    /**
     * 创建项目前端业务
     * @param req
     */
    @Override
    public void createProjectForWeb(ProjectRequest req) {
        PmsProjectInfoEntity project = createProject(req.getProjectName(), req.getProjectCode(), req.getDescription());
        //添加项目负责人
        if (req.getMasterUserId() == null||req.getMasterUserId()==0) {
            throw new BizException(PROJECT_MASTER_ERROR.getCode(), PROJECT_MASTER_ERROR.getDesc());
        }
        //master添加异常回滚gitlab
        try {
            createUserProjectRelationMaster(project.getId(),req.getMasterUserId(),project.getGitlabGroupId());
        }catch (Exception e){
            deleteProject(project.getId(),project.getGitlabGroupId());
            throw new BizException(PROJECT_MASTER_INTO_ERROR.getCode(), PROJECT_MASTER_INTO_ERROR.getDesc());
        }
        //添加项目环境相关信息
        try {

            if (req.getEnvList()!=null&&req.getEnvList().size() > 0) {
                for (net.atayun.bazooka.pms.api.EnvDto envDto : req.getEnvList()) {
                    //查询环境所在集群
                    Long clusterId= envApi.get(envDto.getEnvId()).ifNotSuccessThrowException().getData().getClusterId();
                    net.atayun.bazooka.pms.api.EnvDto insertEnvInfo= queryDistributePort(envDto.getEnvId(),clusterId);
                    createEnvProjectRelation(project.getId(), insertEnvInfo.getEnvId(),
                            insertEnvInfo.getPortStart(), insertEnvInfo.getPortEnd(),clusterId);
                }
            }
        }catch (Exception e){
            throw new BizException(PROJECT_SKIP_ERROR.getCode(), "环境添加失败");
        }
        //添加项目参与人
        try {
            if (req.getDevUserIds() != null&&req.getDevUserIds().size()>0) {
                bathInsertDev(req.getDevUserIds(),project.getId(),project.getGitlabGroupId());
            }
        }catch (BizException e){
            throw new BizException(PROJECT_SKIP_ERROR.getCode(), e.getDesc());
        }

    }

    @Override
    public void bathInsertDev(List<DevUserInfo> devIds, Long projectId, Integer gitlabGroupId) {

        StringBuilder devInfo=new StringBuilder();
        //todo 后期性能问题再修改
        for(DevUserInfo item:devIds){
            if(item.getUserId()>0){
                String ids=item.getUserName();
                try {
                    pmsUserProjectRelationService.addUserForProject(item.getUserId(),projectId,UserTypeEnum.USER_PROJECT_DEV,gitlabGroupId);
                }catch (Exception e){
                    devInfo.append(ids);
                    devInfo.append(",");
                }
            }
        }
        if(!devInfo.toString().equals("")) {
            throw new BizException(PROJRDCT_DEV_ERROR.getCode(),devInfo.toString()+PROJRDCT_DEV_ERROR.getDesc());
        }
    }

    @Override
    public UserTypeEnum queryUserInProject(Long projectId, Long userId) {
        PmsUserProjectRelationEntity entity=new PmsUserProjectRelationEntity();
        entity.setProjectId(projectId);
        entity.setUserId(userId);
        List<PmsUserProjectRelationDto> dto= pmsUserProjectRelationService.select(entity);
        if(dto==null||dto.size()==0){
            return null;
        }else {
          PmsUserProjectRelationDto firstRole= dto.stream().sorted(Comparator.comparing(PmsUserProjectRelationDto::getRoleType).reversed()).findFirst().get();
          return firstRole.getRoleType();

        }
    }


    @Override
    public List<PmsProjectInfoDto> queryProjectListForAdmin(Long userId) {
        //是否是管理人员
        if (userApi.isAdmin(userId).ifNotSuccessThrowException().getData()) {
            PmsProjectInfoEntity entity=new PmsProjectInfoEntity();
            entity.setIsDeleted(IsDeleted.NOT_DELETED);
            return pmsProjectInfoService.select(entity);
        }else {
           return pmsProjectInfoService.queryProjectListForAdmin(userId);
        }
    }

    /**
     * 删除项目逻辑删除
     * @param projectId
     */
    @Override
    @Transactional
    public void deleteProjectNotReal(Long projectId) {
        AppInfoEntity entity= new AppInfoEntity();
        entity.setProjectId(projectId);
        entity.setIsDeleted(IsDeleted.NOT_DELETED);
        List<AppInfoDto> appList= appInfoService.select(entity);
        if(appList!=null&& appList.size()>0) {
            for (AppInfoDto item:appList){
                appService.deleteAppInfo(item.getId());
            }
        }
        pmsProjectInfoService.deleteProject(projectId);
    }

    /**
     * 更新项目信息,减少多次查库操作
     * @param req
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProjectForWeb(UpdateProjectReq req){
        checkIsRepeat(req.getProjectName(),"",req.getProjectId());
        PmsProjectInfoEntity projectInfoEntity=new PmsProjectInfoEntity();
        projectInfoEntity.setId(req.getProjectId());
        projectInfoEntity.setDescription(req.getDescription());
        projectInfoEntity.setProjectName(req.getProjectName());
        pmsProjectInfoService.updateByPrimaryKeySelective(projectInfoEntity);
        projectInfoEntity=pmsProjectInfoService.selectEntityByPrimaryKey(req.getProjectId());
        PmsUserProjectRelationEntity masterInfo=  pmsUserProjectRelationService.queryProjectMaster(req.getProjectId());
        //master变化
        if(req.getMasterUserId()!=null&&req.getMasterUserId()>0) {
            //master没有添加用户
            if(masterInfo==null) {
                pmsUserProjectRelationService.addUserForProject(req.getMasterUserId(),req.getProjectId()
                        ,UserTypeEnum.USER_PROJECT_MASTER,projectInfoEntity.getGitlabGroupId());
            }
            //master变化执行相关操作
            else if(!masterInfo.getUserId().equals(req.getMasterUserId())) {
                pmsUserProjectRelationService.changeUserForProjectSameRole(masterInfo.getUserId(),
                        req.getMasterUserId(),req.getProjectId(),UserTypeEnum.USER_PROJECT_MASTER,
                        projectInfoEntity.getGitlabGroupId());
            }
        }

        //env变化
        if(req.getEnvList()!=null&&req.getEnvList().size()>0){
            List<Long> oldEnvIds= pmsProjectEnvRelationService.queryEnvListForProject(req.getProjectId());
            for (net.atayun.bazooka.pms.api.EnvDto envDto : req.getEnvList()) {
                if(oldEnvIds.size()>0){
                   if(oldEnvIds.contains(envDto.getEnvId())){
                       throw new BizException(ENV_NOT_CHANGE.getCode(),ENV_NOT_CHANGE.getDesc());
                   }
                }
                Long clusterId= envApi.get(envDto.getEnvId()).ifNotSuccessThrowException().getData().getClusterId();
                net.atayun.bazooka.pms.api.EnvDto insertEnvInfo= queryDistributePort(envDto.getEnvId(),clusterId);
                createEnvProjectRelation(req.getProjectId(), insertEnvInfo.getEnvId(), insertEnvInfo.getPortStart(),
                        insertEnvInfo.getPortEnd(),clusterId);
            }
        }
    }


    /**
     * 关联项目用户信息
     * @param projectId
     * @param userId
     */

    private void createUserProjectRelationMaster(Long projectId, Long userId,Integer gitlabGroupId) {
        //checkProjectMaster(projectId, userId);
        pmsUserProjectRelationService.addUserForProject(userId, projectId, UserTypeEnum.USER_PROJECT_MASTER,gitlabGroupId);
    }

    /**
     * 检查项目master目前一个项目一个master
     *
     * @param projectId
     * @param userId
     */
    private void checkProjectMaster(Long projectId, Long userId) {
        PmsUserProjectRelationEntity entity = new PmsUserProjectRelationEntity();
        entity.setProjectId(projectId);
        entity.setUserId(userId);
        entity.setRoleType(UserTypeEnum.USER_PROJECT_MASTER);
        List<PmsUserProjectRelationEntity> entities = pmsUserProjectRelationService.selectEntity(entity);
        if (entities.size() > 0) {
            throw new BizException(PROJECT_MASTER_ERROR.getCode(), PROJECT_MASTER_ERROR.getDesc());
        }
    }


    @Override
    public void createEnvProjectRelation(Long projectId, Long envId, int startPort, int endPort,Long clusterId) {
        //checkEnvPort(envId, startPort, endPort);
        PmsProjectEnvRelationEntity envRelationEntity = new PmsProjectEnvRelationEntity();
        envRelationEntity.setEnvId(envId);
        envRelationEntity.setProjectId(projectId);
        envRelationEntity.setPortStart(startPort);
        envRelationEntity.setPortEnd(endPort);
        envRelationEntity.setClusterId(clusterId);
        pmsProjectEnvRelationService.insertSelective(envRelationEntity);
    }

    @Override
    public void updateEnvProjectRelation(Long projectId, Long envId, int startPort, int endPort) {

    }

    /**
     * 获取项目相关统计
     * @param userId
     * @param keyWord
     * @return
     */
    @Override
    public List<ProjectCountDto> queryProjectCount(Long userId,String keyWord) {
        List<ProjectCountDto> projectCountDtos=  pmsProjectInfoService.queryProjectCount(keyWord);
        if (userApi.isAdmin(userId).ifNotSuccessThrowException().getData()) {
            log.info("用户是管理员");
            return projectCountDtos;

        }
        List<PmsUserProjectRelationEntity>  projectListEntity= pmsUserProjectRelationService.queryProjectByUser(userId);
        List<Long> projectList=projectListEntity.stream().map(m->m.getProjectId()).collect(Collectors.toList());
        projectCountDtos=projectCountDtos.stream().filter(m->projectList.contains(m.getProjectId())).collect(Collectors.toList());
        return projectCountDtos;
    }
    /**
     * 检查环境mlb端口分配
     *
     * @param envId
     * @param startPort
     * @param endPort
     */
    private void checkEnvPort(long envId, int startPort, int endPort) {
        PmsProjectEnvRelationEntity envRelationEntity = new PmsProjectEnvRelationEntity();
        envRelationEntity.setEnvId(envId);
        List<PmsProjectEnvRelationEntity> oldRelations = pmsProjectEnvRelationService.selectEntity(envRelationEntity);
        for (PmsProjectEnvRelationEntity item : oldRelations) {
            if (endPort < item.getPortStart() && startPort > item.getPortEnd()) {
                throw new BizException(PORT_EXIST.getCode(), item.getEnvId() + PORT_EXIST.getDesc());
            }
        }

    }

    @Override
    public void deleteProject(Long Id,Integer gitlabGroupId) {
        pmsProjectInfoService.deleteByPrimaryKey(Id);
        projectExtra.deleteProject(gitlabGroupId);
    }

    /**
     * 根据项目id获取项目信息
     * @param id
     * @return
     */
    @Override
    public ProjectInfoDto queryProjectById(long id) {
      return pmsProjectInfoService.queryProjectById(id);
    }

    /**
     * 获取项目列表
     * @return
     */
    @Override
    public List<ProjectInfoDto> queryProjectLit() {
        List<PmsProjectInfoEntity> entities = pmsProjectInfoService.selectAllEntity();
        if (entities.size() == 0) {
            return null;
        }
        List<ProjectInfoDto> dtos = BaseBeanUtils.copy(entities, ProjectInfoDto.class);
        return dtos;
    }
    /**
     * 根据用户id获取参与已经负责项目
     * @param userId
     * @return
     */
    @Override
    public List<ProjectUserDto> queryProjectLitByUser(Long userId) {
        PmsUserProjectRelationEntity entity=new PmsUserProjectRelationEntity();
        entity.setUserId(userId);
        List<PmsUserProjectRelationEntity> entities = pmsUserProjectRelationService.selectEntity(entity);
        if (entities.size() == 0) {
            return null;
        }
        List<ProjectUserDto> dtos = BaseBeanUtils.copy(entities, ProjectUserDto.class);
        return dtos;
    }

    @Override
    public List<PmsProjectEnvRelationEntity> queryProjectEnvInfo(Long projectId) {
        PmsProjectEnvRelationEntity envEntity=new PmsProjectEnvRelationEntity();
        envEntity.setProjectId(projectId);
        List<PmsProjectEnvRelationEntity> envList= pmsProjectEnvRelationService.selectEntity(envEntity);
        return envList;
    }

    @Override
    public List<PmsProjectEnvRelationEntity> queryProjectByCluster(Long clusterId) {
        PmsProjectEnvRelationEntity envRelationEntity=new PmsProjectEnvRelationEntity();
        envRelationEntity.setClusterId(clusterId);
        List<PmsProjectEnvRelationEntity> envList= pmsProjectEnvRelationService.selectEntity(envRelationEntity);
        return envList;
    }

    @Override
    public List<PmsProjectEnvRelationEntity> queryProjectByEnvNotDelete(Long envId) {
        List<PmsProjectEnvRelationEntity> envList= pmsProjectEnvRelationService.selectProjectNotDelete(envId);
        return envList;
    }

    @Override
    public PmsProjectEnvRelationEntity queryProjectEnvInfo(Long projectId, Long envId) {
        PmsProjectEnvRelationEntity envEntity=new PmsProjectEnvRelationEntity();
        envEntity.setProjectId(projectId);
        envEntity.setEnvId(envId);
        return this.pmsProjectEnvRelationService.selectOneEntity(envEntity);
    }

    /**
     * 根据环境获取可分配端口
     * @param envId
     * @return
     */
    @Override
    public net.atayun.bazooka.pms.api.EnvDto queryDistributePort(Long envId, Long clusterId){
        net.atayun.bazooka.pms.api.EnvDto envDto =new EnvDto();
        List<PmsProjectEnvRelationEntity> envList= queryProjectByCluster(clusterId);
        //库里没有为初始值
        if(envList.size()==0){
            envDto.setEnvId(envId);
            envDto.setPortStart(PmsConstant.START_PORT);
            envDto.setPortEnd(PmsConstant.START_PORT+PmsConstant.PORT_STEP);
            return envDto;
        }else {
            int startPort= getStartPort(envList);
            envDto.setEnvId(envId);
            envDto.setPortStart(startPort);
            envDto.setPortEnd(startPort+PmsConstant.PORT_STEP);
        }
        return envDto;
    }
    private Integer getStartPort(List<PmsProjectEnvRelationEntity> envList){
       int maxEndPort= envList.stream().max(Comparator.comparing(PmsProjectEnvRelationEntity::getPortEnd)).get().getPortEnd();
       return maxEndPort+1;
    }
}
