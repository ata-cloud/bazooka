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

import net.atayun.bazooka.upms.api.dto.req.UserQueryReqDTO;
import net.atayun.bazooka.upms.biz.dal.entity.User;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 用户mapper
 */
public interface UserMapper extends YyMapper<User> {

    /**
     * 查询用户
     *
     * @param userQueryReqDTO
     * @return
     */
    List<User> getPage(UserQueryReqDTO userQueryReqDTO);

    /**
     * 根据用户id查询用户角色名称
     *
     * @param userId
     * @return
     */
    List<String> getRoleNameByUserId(Long userId);

    /**
     * 根据用户id查询权限角色名称
     *
     * @param userId
     * @return
     */
    List<String> getPermissionNameByUserId(Long userId);

    /**
     * 根据用户id查询权限url地址
     *
     * @param userId
     * @return
     */
    List<String> getPermissionUrlByUserId(Long userId);

    /**
     * 该用户是否含有该url权限
     *
     * @param userId
     * @param url
     * @return
     */
    int countByUserIdUrl(@Param("userId") Long userId, @Param("url") String url);

    /**
     * 根据用户名或者邮箱查询用户
     *
     * @param username
     * @param email
     * @return
     */
    User getByUserNameOrEmail(@Param("username") String username, @Param("email") String email);
}
