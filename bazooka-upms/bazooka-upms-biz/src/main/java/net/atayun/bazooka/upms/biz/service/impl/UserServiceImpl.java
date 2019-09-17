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
import net.atayun.bazooka.base.tuple.Tuple2;
import net.atayun.bazooka.upms.biz.component.properties.ShiroProperties;
import net.atayun.bazooka.upms.biz.component.realm.ShiroAuthRealm;
import net.atayun.bazooka.upms.biz.dal.dao.PermissionMapper;
import net.atayun.bazooka.upms.biz.dal.dao.UserMapper;
import net.atayun.bazooka.upms.biz.dal.dao.UserRoleMapper;
import net.atayun.bazooka.upms.biz.dal.entity.Permission;
import net.atayun.bazooka.upms.biz.dal.entity.User;
import net.atayun.bazooka.upms.biz.dal.entity.UserRole;
import net.atayun.bazooka.upms.biz.service.UserService;
import com.github.pagehelper.PageInfo;
import com.youyu.common.api.PageData;
import net.atayun.bazooka.upms.api.dto.req.*;
import net.atayun.bazooka.upms.api.dto.rsp.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.atayun.bazooka.base.utils.BizExceptionUtil.exception;
import static net.atayun.bazooka.base.utils.BizExceptionUtil.exception2MatchingExpression;
import static net.atayun.bazooka.base.utils.OrikaCopyUtil.copyProperty;
import static net.atayun.bazooka.base.utils.OrikaCopyUtil.copyProperty4List;
import static net.atayun.bazooka.base.utils.PageDataUtil.pageInfo2PageData;
import static net.atayun.bazooka.base.utils.StringUtil.eq;
import static net.atayun.bazooka.upms.api.enums.PermissionTypeEnum.MENU;
import static net.atayun.bazooka.upms.api.enums.RoleTypeEnum.ADMIN;
import static net.atayun.bazooka.upms.api.enums.UpmsResultCode.*;
import static net.atayun.bazooka.upms.biz.component.realm.ShiroAuthRealm.getUserId;
import static net.atayun.bazooka.upms.biz.helper.constant.PermissionConstant.ROOT_PERMISSION;
import static net.atayun.bazooka.upms.biz.helper.exception.ExceptionHelper.loginException;
import static com.github.pagehelper.page.PageMethod.startPage;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 用户service impl
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private BatchService batchService;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private ShiroProperties shiroProperties;

    @Autowired
    private ShiroAuthRealm shiroAuthRealm;

    @Override
    public UserLoginRspDTO login(UserLoginReqDTO userLoginReqDTO) {
        try {
            return shiroAuthRealm.login(userLoginReqDTO);
        } catch (Exception ex) {
            throw loginException(ex);
        }
    }

    @Override
    public void logout() {
        shiroAuthRealm.logout();
    }

    @Override
    public void modifyPassword(UserModifyPasswordReqDTO userModifyPasswordReqDTO) {
        User user = userMapper.selectByPrimaryKey(getUserId());
        checkUser(user);

        user.modifyPassword(userModifyPasswordReqDTO, shiroProperties.getHashAlgorithmName());
        userMapper.updateByPrimaryKey(user);
        shiroAuthRealm.logout();
    }


    @Override
    public User getAuthenticationUser(String username, String password) {
        User queryUser = new User(username, password);
        User user = userMapper.selectOne(queryUser);
        checkUser(user);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(UserAddReqDTO userAddReqDTO) {
        checkUserAdd(userAddReqDTO);
        User user = new User(userAddReqDTO, shiroProperties.getHashAlgorithmName());
        userMapper.insertSelective(user);

        saveUserRoles(user, userAddReqDTO.getRoleIds());
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(UserDeleteReqDTO userDeleteReqDTO) {
        Long userId = userDeleteReqDTO.getUserId();
        userMapper.deleteByPrimaryKey(userId);
        userRoleMapper.deleteByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(UserEditReqDTO userEditReqDTO) {
        User existUser = userMapper.selectByPrimaryKey(userEditReqDTO.getUserId());
        if (!eq(existUser.getEmail(), userEditReqDTO.getEmail())) {
            checkUserEmail(userEditReqDTO.getEmail());
        }

        User user = new User(userEditReqDTO, shiroProperties.getHashAlgorithmName());
        userMapper.updateByPrimaryKeySelective(user);
        updateUserRoles(user, userEditReqDTO.getRoleIds());
    }

    @Override
    public PageData<UserQueryRspDTO> getPage(UserQueryReqDTO userQueryReqDTO) {
        startPage(userQueryReqDTO.getPageNo(), userQueryReqDTO.getPageSize());
        List<User> users = userMapper.getPage(userQueryReqDTO);
        PageInfo userPage = new PageInfo(users);

        List<UserQueryRspDTO> userQueryRsps = copyProperty4List(users, UserQueryRspDTO.class);
        fillUserRole(userQueryRsps);
        return pageInfo2PageData(userPage, userQueryRsps);
    }

    @Override
    public void unauthorized() {
        exception(USER_UNAUTHORIZED);
    }

    @Override
    public void needLogin() {
        exception(ACCESS_EXCEPTION_NEED_LOGIN);
    }

    @Override
    public Tuple2<Set<String>, Set<String>> getRolePermissionTuple2(Long userId) {
        List<String> roles = userMapper.getRoleNameByUserId(userId);
        List<String> permissions = userMapper.getPermissionNameByUserId(userId);

        return new Tuple2<>(new HashSet<>(roles), new HashSet<>(permissions));
    }

    @Override
    public boolean hasUrlPermission(Long userId, String url) {
        int count = userMapper.countByUserIdUrl(userId, url);
        return count == 0 ? false : true;
    }

    @Override
    public List<UserMenuPermissionRspDTO> getUserMenuPermissions(UserMenuPermissionReqDTO userPermissionReqDTO) {
        List<Permission> rootPermissions = permissionMapper.getPermissionByUserIdParentIdPermissionType(getUserId(), ROOT_PERMISSION, MENU.getCode());
        return copyProperty4List(rootPermissions, UserMenuPermissionRspDTO.class);
    }

    @Override
    public UserDetailRspDTO getUserDetail(Long userId) {
        User user = getUser(userId);
        return copyProperty(user, UserDetailRspDTO.class);
    }

    @Override
    public Boolean isAdmin(Long userId) {
        userId = isNull(userId) ? getUserId() : userId;
        User user = getUser(userId);
        Integer count = userRoleMapper.countByUserIdRoleType(user.getId(), ADMIN.getCode());
        return count > 0 ? true : false;
    }

    /**
     * 根据用户id查询用户信息
     *
     * @param userId
     * @return
     */
    private User getUser(Long userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        return user;
    }

    /**
     * 填充用户角色信息
     *
     * @param users
     */
    private void fillUserRole(List<UserQueryRspDTO> users) {
        if (isEmpty(users)) {
            return;
        }

        for (UserQueryRspDTO user : users) {
            List<UserRoleRspDTO> userRoles = userRoleMapper.getRoleByUserId(user.getUserId());
            user.setUserRoles(userRoles);
        }
    }

    /**
     * 更新用户角色
     *
     * @param user
     * @param roleIds
     */
    private void updateUserRoles(User user, List<Long> roleIds) {
        userRoleMapper.deleteByUserId(user.getId());
        saveUserRoles(user, roleIds);
    }

    /**
     * 保存用户角色信息
     *
     * @param user
     * @param roleIds
     */
    private void saveUserRoles(User user, List<Long> roleIds) {
        if (isEmpty(roleIds)) {
            return;
        }

        List<UserRole> userRoles = new ArrayList<>();
        for (Long roleId : roleIds) {
            userRoles.add(new UserRole(user.getId(), roleId));
        }
        batchService.batchDispose(userRoles, UserRoleMapper.class, "insertSelective");
    }

    /**
     * 检查用户添加
     *
     * @param userAddReqDTO
     */
    private void checkUserAdd(UserAddReqDTO userAddReqDTO) {
        User user = userMapper.getByUserNameOrEmail(userAddReqDTO.getUsername(), userAddReqDTO.getEmail());
        exception2MatchingExpression(nonNull(user), USERNAME_OR_EMAIL_ALREADY_EXIST);
    }

    /**
     * 验证user
     *
     * @param user
     */
    private void checkUser(User user) {
        exception2MatchingExpression(isNull(user), USERNAME_OR_PASSWORD_ERROR);
        user.checkStatus();
    }

    /**
     * 检查用户邮箱是否存在
     *
     * @param email
     */
    private void checkUserEmail(String email) {
        User queryUser = new User();
        queryUser.setEmail(email);
        User user = userMapper.selectOne(queryUser);
        exception2MatchingExpression(nonNull(user), EMAIL_ALREADY_EXIST);
    }

}
