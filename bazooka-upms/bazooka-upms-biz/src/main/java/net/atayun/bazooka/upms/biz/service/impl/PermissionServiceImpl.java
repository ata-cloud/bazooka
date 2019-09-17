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
import net.atayun.bazooka.upms.api.dto.req.*;
import net.atayun.bazooka.upms.api.dto.rsp.PermissionQueryRspDTO;
import net.atayun.bazooka.upms.api.dto.rsp.PermissionTreeRspDTO;
import net.atayun.bazooka.upms.biz.dal.dao.PermissionMapper;
import net.atayun.bazooka.upms.biz.dal.dao.RolePermissionMapper;
import net.atayun.bazooka.upms.biz.dal.entity.Permission;
import net.atayun.bazooka.upms.biz.dal.entity.RolePermission;
import net.atayun.bazooka.upms.biz.service.PermissionService;
import com.github.pagehelper.PageInfo;
import com.youyu.common.api.PageData;
import net.atayun.bazooka.upms.biz.helper.constant.PermissionConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static net.atayun.bazooka.base.utils.OrikaCopyUtil.copyProperty4List;
import static net.atayun.bazooka.base.utils.PageDataUtil.pageInfo2PageData;
import static com.github.pagehelper.page.PageMethod.startPage;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 权限service impl
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private BatchService batchService;

    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(PermissionAddReqDTO permissionAddReqDTO) {
        Permission permission = new Permission(permissionAddReqDTO);
        permissionMapper.insertSelective(permission);

        saveRolePermissions(permission, permissionAddReqDTO.getRoleIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(PermissionDeleteReqDTO permissionDeleteReqDTO) {
        Long permissionId = permissionDeleteReqDTO.getPermissionId();
        permissionMapper.deleteByPrimaryKey(permissionId);
        rolePermissionMapper.deleteByPermissionId(permissionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(PermissionEditReqDTO permissionEditReqDTO) {
        Permission permission = new Permission(permissionEditReqDTO);
        permissionMapper.updateByPrimaryKeySelective(permission);
    }

    @Override
    public PageData<PermissionQueryRspDTO> getPage(PermissionQueryReqDTO permissionQueryReqDTO) {
        startPage(permissionQueryReqDTO.getPageNo(), permissionQueryReqDTO.getPageSize());
        List<Permission> permissions = permissionMapper.getPage(permissionQueryReqDTO);
        PageInfo<Permission> permissionPage = new PageInfo<>(permissions);

        List<PermissionQueryRspDTO> permissionQueryRsps = copyProperty4List(permissions, PermissionQueryRspDTO.class);
        return pageInfo2PageData(permissionPage, permissionQueryRsps);
    }

    @Override
    public boolean isUrlExist(String url) {
        int count = permissionMapper.countByUrl(url);
        return count == 0 ? false : true;
    }

    @Override
    public List<PermissionTreeRspDTO> getPermissionTree(PermissionTreeReqDTO permissionTreeReqDTO) {
        List<Permission> rootPermissions = permissionMapper.getPermissionByParentId(PermissionConstant.ROOT_PERMISSION);
        return copyProperty4List(rootPermissions, PermissionTreeRspDTO.class);
    }

    /**
     * 保存角色权限信息
     *
     * @param permission
     * @param roleIds
     */
    private void saveRolePermissions(Permission permission, List<Long> roleIds) {
        if (isEmpty(roleIds)) {
            return;
        }

        List<RolePermission> rolePermissions = new ArrayList<>();
        for (Long roleId : roleIds) {
            rolePermissions.add(new RolePermission(roleId, permission.getId()));
        }
        batchService.batchDispose(rolePermissions, RolePermissionMapper.class, "insertSelective");
    }
}
