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
package net.atayun.bazooka.deploy.biz.dal.dao.deploy;

import net.atayun.bazooka.deploy.biz.dal.entity.deploy.DeployCountsEntity;
import net.atayun.bazooka.deploy.biz.dal.entity.deploy.DeployEntity;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Ping
 * @date 2019-07-11
 */
public interface DeployMapper extends YyMapper<DeployEntity> {

    /**
     * 统计服务发布次数
     *
     * @param projectId    项目ID
     * @param leftDatetime 起始日期
     * @return list
     */
    @Select("select " +
            "app_id, " +
            "app_name, " +
            "count(0) counts " +
            "from deploy_deploy " +
            "where project_id = #{projectId} and create_time >= #{leftDatetime} " +
            "group by app_id, app_name " +
            "order by counts desc;")
    List<DeployCountsEntity> deployCountsByProject(@Param("projectId") Long projectId, @Param("leftDatetime") LocalDateTime leftDatetime);

    /**
     * 查询最新一条发布记录
     *
     * @param appId appId
     * @param envId
     * @return
     */
    @Select("select " +
            "* " +
            "from deploy_deploy " +
            "where app_id = #{appId} and env_id = #{envId} " +
            "order by update_time desc " +
            "limit 1;")
    DeployEntity selectLastOne(@Param("appId") Long appId, @Param("envId") Long envId);
}
