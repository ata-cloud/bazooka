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

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Sets;
import com.youyu.common.api.PageData;
import net.atayun.bazooka.upms.api.dto.req.*;
import net.atayun.bazooka.upms.api.dto.rsp.PermissionQueryRspDTO;
import net.atayun.bazooka.upms.api.dto.rsp.PermissionTreeRspDTO;
import net.atayun.bazooka.upms.biz.repository.PermissionRepository;
import net.atayun.bazooka.upms.biz.repository.RoleRepository;
import net.atayun.bazooka.upms.biz.domain.Permission;
import net.atayun.bazooka.upms.biz.domain.Role;
import net.atayun.bazooka.upms.biz.helper.constant.PermissionConstant;
import net.atayun.bazooka.upms.biz.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static net.atayun.bazooka.combase.utils.OrikaCopyUtil.copyProperty4List;
import static net.atayun.bazooka.combase.utils.PageDataUtil.pageInfo2PageData;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 权限service impl
 */
@Service
public class PermissionServiceImpl implements PermissionService {


    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(PermissionAddReqDTO permissionAddReqDTO) {
        Permission permission = new Permission(permissionAddReqDTO);

        Set<Role> roles =  Sets.newHashSet(roleRepository.findAllById(permissionAddReqDTO.getRoleIds()));
        permission.setRoles(roles);
        permissionRepository.save(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(PermissionDeleteReqDTO permissionDeleteReqDTO) {
        Long permissionId = permissionDeleteReqDTO.getPermissionId();
        permissionRepository.deleteById(permissionId);
//        permissionMapper.deleteByPrimaryKey(permissionId);
//        rolePermissionMapper.deleteByPermissionId(permissionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(PermissionEditReqDTO permissionEditReqDTO) {
        Permission permission = new Permission(permissionEditReqDTO);
//        permissionMapper.updateByPrimaryKeySelective(permission);
        permissionRepository.save(permission);
    }

    @Override
    public PageData<PermissionQueryRspDTO> getPage(PermissionQueryReqDTO permissionQueryReqDTO) {
//        startPage(permissionQueryReqDTO.getPageNo(), permissionQueryReqDTO.getPageSize());
//        List<Permission> permissions = permissionMapper.getPage(permissionQueryReqDTO);
        Pageable pageable = Pageable.unpaged();
        //TODO fix
        List<Permission> permissions = permissionRepository.findAllByPermissionIdAndPermissionName(permissionQueryReqDTO.getPermissionId(),permissionQueryReqDTO.getPermissionName(),pageable);
        PageInfo<Permission> permissionPage = new PageInfo<>(permissions);

        List<PermissionQueryRspDTO> permissionQueryRsps = copyProperty4List(permissions, PermissionQueryRspDTO.class);
        return pageInfo2PageData(permissionPage, permissionQueryRsps);
    }

    @Override
    public boolean isUrlExist(String url) {
        long count = permissionRepository.countByUrl(url);
        return count == 0 ? false : true;
    }

    @Override
    public List<PermissionTreeRspDTO> getPermissionTree(PermissionTreeReqDTO permissionTreeReqDTO) {
        List<Permission> rootPermissions = permissionRepository.findPermissionsByParentId(PermissionConstant.ROOT_PERMISSION);
        return copyProperty4List(rootPermissions, PermissionTreeRspDTO.class);
    }

}
