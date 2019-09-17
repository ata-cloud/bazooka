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
package net.atayun.bazooka.pms.api;

import net.atayun.bazooka.base.page.PageQuery;
import net.atayun.bazooka.pms.api.dto.EnvDto;
import net.atayun.bazooka.pms.api.vo.DevUserResponse;
import net.atayun.bazooka.pms.api.vo.GitlabUserRequest;
import net.atayun.bazooka.pms.api.vo.ProjectResponse;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * @author rache
 * @date 2019-07-25
 */
public interface ProjectApi {
    /**
     * 根据环境获取项目数量
     * @param envId
     * @return
     */
    Result<Integer> countProject(Long envId);
    @ApiOperation(value = "添加gitlab用户")
    @PostMapping("/project/addGitlabUser")
    Result addGitlabUser(GitlabUserRequest request);

    Result<List<EnvDto>> queryEnvPortList(Long projectId);

    Result<PageData<DevUserResponse>> queryDevUser(Long projectId, PageQuery query);

    Result<ProjectResponse> queryProjectInfo(Long projectId);


    @ApiOperation(value = "获取用户所拥有环境")
    Result<List<Long>> queryEnvForUser(@PathVariable Long userId);
}
