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
package net.atayun.bazooka.pms.biz.service;

import net.atayun.bazooka.pms.api.dto.AppInfoDto;
import net.atayun.bazooka.pms.api.enums.ConfigTitleEnum;
import net.atayun.bazooka.pms.api.vo.ProjectShowResponse;

import java.util.List;

/**
 * @author rache
 * @date 2019-07-18
 */

public interface SortService {

    /**
     * 应用列表置顶排序
     *
     * @param appInfoDtoList
     * @param userId
     * @return
     */
    List<AppInfoDto> appListTopOrder(List<AppInfoDto> appInfoDtoList, long userId);

    /**
     * 项目置顶
     * @param projectList
     * @param userId
     * @param configTitle
     * @return
     */
    List<ProjectShowResponse> topProject(List<ProjectShowResponse> projectList, long userId, ConfigTitleEnum configTitle);

    void createTopConfig(Long userId, Long projectId, ConfigTitleEnum configTitle);

    void deleteConfig(Long userId, Long projectId, ConfigTitleEnum configTitle);
}
