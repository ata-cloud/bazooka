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
package net.atayun.bazooka.rms.biz.dal.dao;

import net.atayun.bazooka.rms.biz.dal.entity.RmsEnvEntity;
import com.youyu.common.mapper.YyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 代码生成器
 *
 * @author 技术平台
 * @date 2019-07-16
 */
public interface RmsEnvMapper extends YyMapper<RmsEnvEntity> {
    /**
     * 模糊匹配环境列表
     *
     * @param keyword
     * @return
     */
    @Select("<script> " +
            "select id, cluster_id, code, name, state, cpus, memory, disk from rms_env " +
            "where 1 = 1 " +
            "<if test='envIds != null'> " +
            "and id in " +
            "<foreach collection='envIds' item='envId' separator=',' open='(' close=')'> " +
            "#{envId,jdbcType=INTEGER} " +
            "</foreach> " +
            "</if> " +
            "<if test='keyword != null'> " +
            "and (name like concat('%',#{keyword,jdbcType=VARCHAR},'%') or code like concat('%',#{keyword,jdbcType=VARCHAR},'%')) " +
            "</if> " +
            "order by id desc " +
            "</script>")
    List<RmsEnvEntity> selectByCondition(@Param("envIds") List<Long> envIds, @Param("keyword") String keyword);

    /**
     * 检查是否已存在指定环境名称或环境代码
     *
     * @param name
     * @param code
     * @return
     */
    @Select("select code, name from rms_env where name = #{name,jdbcType=VARCHAR} or code = #{code,jdbcType=VARCHAR} ")
    RmsEnvEntity selectByNameOrCode(@Param("name") String name, @Param("code") String code);

    /**
     * 获取集群下环境列表
     *
     * @param clusterId
     * @return
     */
    @Select("select id, code, name, cpus, memory, disk from rms_env where cluster_id = #{clusterId,jdbcType=INTEGER} ")
    List<RmsEnvEntity> selectByClusterId(@Param("clusterId") Long clusterId);

}




