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
package net.atayun.bazooka.gateway.service.rms.impl;

import net.atayun.bazooka.gateway.service.rms.OpsEnvService;
import net.atayun.bazooka.gateway.vo.rms.EnvVo;
import net.atayun.bazooka.pms.api.ProjectApi;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.dto.EnvDto;
import net.atayun.bazooka.rms.api.param.EnvCreateReq;
import net.atayun.bazooka.rms.api.param.EnvModifyReq;
import net.atayun.bazooka.rms.api.param.EnvQueryReq;
import net.atayun.bazooka.upms.api.feign.UserApi;
import com.youyu.common.api.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

import static net.atayun.bazooka.base.subject.OpsThreadLocalSubject.getOpsUser;
import static net.atayun.bazooka.base.utils.CommonUtil.defaultValue;
import static net.atayun.bazooka.base.utils.OrikaCopyUtil.copyProperty;
import static java.lang.Boolean.FALSE;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * @Author: hanxiaorui
 * @Date: 2019/7/25 10:02
 * @Description: 环境信息业务实现
 */
@Slf4j
@Service
public class OpsEnvServiceImpl implements OpsEnvService {

    @Autowired
    private EnvApi envApi;

    @Autowired
    private UserApi userApi;

    @Autowired
    private ProjectApi projectApi;

    @Override
    public List<EnvVo> list(EnvQueryReq envQueryReq) {

        if (!isUserAdmin()) {
            List<Long> userEnvs = getUserEnvs();
            if (isEmpty(userEnvs)) {
                return null;
            }
            envQueryReq.setIds(userEnvs);
        }
        Result<List<EnvDto>> result = envApi.list(envQueryReq);
        // get env list
        List<EnvDto> envs = result.ifNotSuccessThrowException().getData();
        if (isEmpty(envs)) {
            return null;
        }
        return envs.stream().map(envFunction()).collect(toList());
    }

    private boolean isUserAdmin() {

        Result<Boolean> result = userApi.isAdmin(getOpsUser().getUserId());
        return defaultValue(result.ifNotSuccessThrowException().getData(), FALSE);
    }

    private List<Long> getUserEnvs() {

        Result<List<Long>> result = projectApi.queryEnvForUser(getOpsUser().getUserId());
        return result.ifNotSuccessThrowException().getData();
    }

    private Function<EnvDto, EnvVo> envFunction() {
        return (envDto -> {
            EnvVo env = copyProperty(envDto, EnvVo.class);
            // get env projects
            requireNonNull(env).setProjectNum(defaultValue(countEnvProjectNum(env.getId()), 0));
            return env;
        });
    }

    private Integer countEnvProjectNum(Long envId) {

        Result<Integer> result = projectApi.countProject(envId);
        return result.ifNotSuccessThrowException().getData();
    }

    @Override
    public void create(EnvCreateReq envCreateReq) {
        Result result = envApi.create(envCreateReq);
        result.ifNotSuccessThrowException();

    }

    @Override
    public void update(EnvModifyReq envModifyReq) {
        Result result = envApi.update(envModifyReq);
        result.ifNotSuccessThrowException();
    }

    @Override
    public void delete(Long envId) {
        Result result = envApi.delete(envId);
        result.ifNotSuccessThrowException();
    }
}
