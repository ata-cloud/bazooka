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
package net.atayun.bazooka.pms.biz.dal.dao;

import net.atayun.bazooka.pms.api.enums.UserTypeEnum;
import net.atayun.bazooka.pms.biz.dal.entity.PmsUserProjectRelationEntity;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author rache
 * @date 2019-07-12
 */
public interface PmsUserProjectRelationMapper extends YyMapper<PmsUserProjectRelationEntity> {
    Integer updateRelation(@Param("projectId") Long projectId,
                           @Param("userId") Long userId,
                           @Param("roleType") UserTypeEnum roleType);
    List<Long> queryUserEnvInfo(@Param("userId")Long userId);
}
