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

import net.atayun.bazooka.combase.page.PageQuery;
import net.atayun.bazooka.pms.api.dto.PmsUserProjectRelationDto;
import net.atayun.bazooka.pms.api.enums.UserTypeEnum;
import net.atayun.bazooka.pms.api.vo.DevUserResponse;
import net.atayun.bazooka.pms.biz.dal.entity.PmsUserProjectRelationEntity;
import com.youyu.common.api.PageData;
import com.youyu.common.service.IService;

import java.util.List;

/**
 * @author rache
 * @date 2019-07-19
 */
public interface PmsUserProjectRelationService extends IService<PmsUserProjectRelationDto, PmsUserProjectRelationEntity> {
    List<PmsUserProjectRelationEntity> queryUserForProject(Long projectId, UserTypeEnum roleType);
    PageData<DevUserResponse> queryUserForProject(PageQuery pageQuery, Long projectId, UserTypeEnum roleType);

    PmsUserProjectRelationEntity queryProjectMaster(Long projectId);
    List<PmsUserProjectRelationEntity> queryProjectByUser(Long userId);
    void addUserForProject(Long userId,Long projectId,UserTypeEnum roleType);
    void addUserForProject(Long userId,Long projectId,UserTypeEnum roleType,Integer groupId);
    void addUserForProject(Long userId, Long projectId, UserTypeEnum roleType,Integer gitlabGroupId,Integer gitLabUserId);
    void deleteUserForProject(Long userId,Long projectId,UserTypeEnum roleType);
    void deleteUserForProject(Long userId,Long projectId,UserTypeEnum roleType,Integer groupId);
    void deleteUserForProject(Long userId,Long projectId,UserTypeEnum roleType,Integer groupId,Integer gitlabUserId);
    void UpdateUserForProject(Long userId, Long projectId, UserTypeEnum roleType,Integer gitlabGroupI);
    void UpdateUserForProject(Long userId, Long projectId, UserTypeEnum roleType,Integer gitlabGroupId,Integer gitLabUserId);
    void changeUserForProjectSameRole(Long oldUserId,Long newUserId,Long projectId,UserTypeEnum roleType,Integer groupId);
    List<Long> queryEnvForUser(Long userId);
}
