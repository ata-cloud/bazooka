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

import net.atayun.bazooka.pms.api.dto.*;
import net.atayun.bazooka.pms.api.enums.UserTypeEnum;
import net.atayun.bazooka.pms.api.vo.ProjectRequest;
import net.atayun.bazooka.pms.api.vo.UpdateProjectReq;
import net.atayun.bazooka.pms.biz.dal.entity.PmsProjectEnvRelationEntity;
import net.atayun.bazooka.pms.biz.dal.entity.PmsProjectInfoEntity;

import java.util.List;

/**
 * @author rache
 * @date 2019-07-16
 */
public interface ProjectService {

    PmsProjectInfoEntity createProject(String projectName, String projectCode, String description);
    void createProjectForWeb(ProjectRequest projectRequest);
    void updateProjectForWeb(UpdateProjectReq projectRequest);
    void createEnvProjectRelation(Long projectId,Long envId,int startPort,int endPort,Long clusterId);
    void updateEnvProjectRelation(Long projectId,Long envId,int startPort,int endPort);

    /**
     * 获取项目相关统计
     * @param userId
     * @param keyWord
     * @return
     */
    List<ProjectCountDto> queryProjectCount(Long userId, String keyWord);
    void deleteProject(Long Id,Integer gitlabGroupId);

    /**
     * 根据项目id获取项目信息
     * @param id
     * @return
     */
    ProjectInfoDto queryProjectById(long id);

    /**
     * 获取项目列表
     * @return
     */
    List<ProjectInfoDto> queryProjectLit();

    /**
     * 根据用户id获取参与已经负责项目
     * @param userId
     * @return
     */
    List<ProjectUserDto> queryProjectLitByUser(Long userId);

    List<PmsProjectEnvRelationEntity> queryProjectEnvInfo(Long projectId);

    List<PmsProjectEnvRelationEntity> queryProjectByCluster(Long clusterId);
    List<PmsProjectEnvRelationEntity> queryProjectByEnvNotDelete(Long envId);

    PmsProjectEnvRelationEntity queryProjectEnvInfo(Long projectId,Long envId);

    /**
     * 根据环境获取可分配端口
     * @param envId
     * @return
     */
    EnvDto queryDistributePort(Long envId,Long clusterId);
    void bathInsertDev(List<DevUserInfo> devIds, Long projectId, Integer gitlabGroupId);

    UserTypeEnum queryUserInProject(Long projectId,Long userId);
    List<PmsProjectInfoDto> queryProjectListForAdmin(Long userId);
    void deleteProjectNotReal(Long projectId);
}
