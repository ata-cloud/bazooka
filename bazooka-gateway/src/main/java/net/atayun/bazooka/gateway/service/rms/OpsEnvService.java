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
package net.atayun.bazooka.gateway.service.rms;

import net.atayun.bazooka.gateway.vo.rms.EnvVo;
import net.atayun.bazooka.rms.api.param.EnvCreateReq;
import net.atayun.bazooka.rms.api.param.EnvModifyReq;
import net.atayun.bazooka.rms.api.param.EnvQueryReq;

import java.util.List;

/**
 * @Author: hanxiaorui
 * @Date: 2019/7/25 09:59
 * @Description: 环境信息
 */
public interface OpsEnvService {

    /**
     * 查询环境列表信息
     *
     * @param envQueryReq
     * @return
     */
    List<EnvVo> list(EnvQueryReq envQueryReq);

    /**
     * 创建环境信息
     *
     * @param envCreateReq
     */
    void create(EnvCreateReq envCreateReq);

    /**
     * 更新环境信息
     *
     * @param envModifyReq
     */
    void update(EnvModifyReq envModifyReq);

    /**
     * 删除环境信息
     *
     * @param envId
     */
    void delete(Long envId);
}
