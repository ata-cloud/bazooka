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
package net.atayun.bazooka.upms.biz.dal.dao;

import net.atayun.bazooka.upms.api.dto.req.PermissionQueryReqDTO;
import net.atayun.bazooka.upms.biz.dal.entity.Permission;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 权限mapper
 */
public interface PermissionMapper extends YyMapper<Permission> {

    /**
     * 查询权限
     *
     * @param permissionQueryReqDTO
     * @return
     */
    List<Permission> getPage(PermissionQueryReqDTO permissionQueryReqDTO);

    /**
     * 根据url统计
     *
     * @param url
     * @return
     */
    int countByUrl(String url);

    /**
     * 根据用户id查询权限列表
     *
     * @param userId
     * @param parentId
     * @param permissionType
     * @return
     */
    List<Permission> getPermissionByUserIdParentIdPermissionType(@Param("userId") Long userId, @Param("parentId") Long parentId, @Param("permissionType") Integer permissionType);

    /**
     * 根据角色id查询权限id列表
     *
     * @param roleId
     * @return
     */
    List<String> getPermissionIdByRoleId(Long roleId);

    /**
     * 根据父节点查询权限树
     *
     * @param parentId
     * @return
     */
    List<Permission> getPermissionByParentId(Long parentId);
}
