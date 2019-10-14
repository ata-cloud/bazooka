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

import com.youyu.common.api.AppInfo;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.dto.AppInfoDto;
import net.atayun.bazooka.pms.biz.dal.entity.AppDeployConfigEntity;
import net.atayun.bazooka.pms.biz.dal.entity.AppInfoEntity;

import java.util.List;

/**
 * @author WangSongJun
 * @date 2019-07-17
 */
public interface AppService {

    /**
     * 添加应用
     *
     * @param appInfoEntity
     * @return
     */
    AppInfo addAppInfo(AppInfo appInfoEntity);

    /**
     * 更新应用
     *
     * @param appInfoEntity
     * @return
     */
    AppInfoDto updateAppInfo(AppInfoEntity appInfoEntity);

    /**
     * 删除应用
     *
     * @param appId
     * @return
     */
    int deleteAppInfo(Long appId);

    /**
     * 检查服务状态是否可以删除（不能删除正在运行或发布的服务）
     *
     * @param appId
     * @return
     */
    boolean checkAppStatusForDelete(Long appId);

    /**
     * 根据当前用户查询应用列表
     *
     * @param currentUserId
     * @param projectId
     * @param keyword
     * @return
     */
    List<AppInfoDto> getAppInfoListByUser(Long currentUserId, Long projectId, String keyword);

    /**
     * 设置用户的角色（项目负责人、参与人或服务负责人）
     *
     * @param userId
     * @param appInfoDtoList
     * @return
     */
    List<AppInfoDto> setUserRoleType(Long userId, List<AppInfoDto> appInfoDtoList);

    /**
     * MLB 端口检测
     *
     * @param appId
     * @param envId
     * @param port
     * @return
     */
    boolean checkMlbPort(Long appId, Long envId, Integer port);

    /**
     * 保存发布配置
     *
     * @param deployConfigDto
     * @return
     */
    AppDeployConfigEntity saveDeployConfig(AppDeployConfigDto deployConfigDto);

    /**
     * 更新发布配置
     *
     * @param deployConfigDto
     * @return
     */
    AppDeployConfigEntity updateDeployConfig(AppDeployConfigDto deployConfigDto);
}
