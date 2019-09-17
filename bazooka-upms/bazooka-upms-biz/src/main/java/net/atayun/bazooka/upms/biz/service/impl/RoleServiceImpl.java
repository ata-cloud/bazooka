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
package net.atayun.bazooka.upms.biz.service.impl;

import net.atayun.bazooka.base.service.BatchService;
import net.atayun.bazooka.upms.api.dto.req.RoleAddReqDTO;
import net.atayun.bazooka.upms.api.dto.req.RoleDeleteReqDTO;
import net.atayun.bazooka.upms.api.dto.req.RoleEditReqDTO;
import net.atayun.bazooka.upms.api.dto.req.RoleQueryReqDTO;
import net.atayun.bazooka.upms.api.dto.rsp.RoleQueryRspDTO;
import net.atayun.bazooka.upms.biz.dal.dao.PermissionMapper;
import net.atayun.bazooka.upms.biz.dal.dao.RoleMapper;
import net.atayun.bazooka.upms.biz.dal.dao.RolePermissionMapper;
import net.atayun.bazooka.upms.biz.dal.entity.Role;
import net.atayun.bazooka.upms.biz.dal.entity.RolePermission;
import net.atayun.bazooka.upms.biz.service.RoleService;
import com.github.pagehelper.PageInfo;
import com.youyu.common.api.PageData;
import com.youyu.common.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static net.atayun.bazooka.base.utils.OrikaCopyUtil.copyProperty4List;
import static net.atayun.bazooka.base.utils.PageDataUtil.pageInfo2PageData;
import static net.atayun.bazooka.upms.api.enums.UpmsResultCode.ROLE_NAME_ALREADY_EXIST;
import static com.github.pagehelper.page.PageMethod.startPage;
import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 角色service impl
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private BatchService batchService;

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(RoleAddReqDTO roleAddReqDTO) {
        checkRoleName(roleAddReqDTO.getRoleName());
        Role role = new Role(roleAddReqDTO);
        roleMapper.insertSelective(role);

        saveRolePermissions(role, roleAddReqDTO.getPermissionIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(RoleDeleteReqDTO roleDeleteReqDTO) {
        Long roleId = roleDeleteReqDTO.getRoleId();
        roleMapper.deleteByPrimaryKey(roleId);

        rolePermissionMapper.deleteByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(RoleEditReqDTO roleEditReqDTO) {
        Role role = new Role(roleEditReqDTO);
        roleMapper.updateByPrimaryKeySelective(role);

        updateRolePermissions(role, roleEditReqDTO.getPermissionIds());
    }

    @Override
    public PageData<RoleQueryRspDTO> getPage(RoleQueryReqDTO roleQueryReqDTO) {
        startPage(roleQueryReqDTO.getPageNo(), roleQueryReqDTO.getPageSize());
        List<Role> roles = roleMapper.getPage(roleQueryReqDTO);
        PageInfo<Role> rolePage = new PageInfo<>(roles);

        List<RoleQueryRspDTO> roleQueryRsps = copyProperty4List(rolePage.getList(), RoleQueryRspDTO.class);
        fillRolePermission(roleQueryRsps);
        return pageInfo2PageData(rolePage, roleQueryRsps);
    }

    /**
     * 填充角色对应权限列表
     *
     * @param roles
     */
    private void fillRolePermission(List<RoleQueryRspDTO> roles) {
        if (isEmpty(roles)) {
            return;
        }

        for (RoleQueryRspDTO role : roles) {
            role.setPermissionIds(permissionMapper.getPermissionIdByRoleId(role.getRoleId()));
        }
    }

    /**
     * 检查角色名是否已经存在
     *
     * @param roleName
     */
    private void checkRoleName(String roleName) {
        Role queryRole = new Role(roleName);
        Role role = roleMapper.selectOne(queryRole);
        if (nonNull(role)) {
            throw new BizException(ROLE_NAME_ALREADY_EXIST);
        }
    }

    /**
     * 更新角色权限信息
     *
     * @param role
     * @param permissionIds
     */
    private void updateRolePermissions(Role role, List<Long> permissionIds) {
        rolePermissionMapper.deleteByRoleId(role.getId());
        saveRolePermissions(role, permissionIds);
    }

    /**
     * 保存角色权限信息
     *
     * @param role
     * @param permissionIds
     */
    private void saveRolePermissions(Role role, List<Long> permissionIds) {
        if (isEmpty(permissionIds)) {
            return;
        }

        List<RolePermission> rolePermissions = new ArrayList<>();
        for (Long permissionId : permissionIds) {
            rolePermissions.add(new RolePermission(role.getId(), permissionId));
        }
        batchService.batchDispose(rolePermissions, RolePermissionMapper.class, "insertSelective");
    }
}
