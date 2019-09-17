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

import net.atayun.bazooka.upms.api.dto.rsp.UserRoleRspDTO;
import net.atayun.bazooka.upms.biz.dal.entity.UserRole;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 用户角色mapper
 */
public interface UserRoleMapper extends YyMapper<UserRole> {

    /**
     * 删除用户关联权限
     *
     * @param userId
     */
    void deleteByUserId(Long userId);

    /**
     * 根据用户id查询用户关联角色
     *
     * @param userId
     * @return
     */
    List<UserRoleRspDTO> getRoleByUserId(Long userId);

    /**
     * 根据用户id和角色类型统计
     *
     * @param userId
     * @param roleType
     * @return
     */
    Integer countByUserIdRoleType(@Param("userId") Long userId, @Param("roleType") Long roleType);
}
