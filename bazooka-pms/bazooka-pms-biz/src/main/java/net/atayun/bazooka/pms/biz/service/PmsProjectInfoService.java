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

import net.atayun.bazooka.pms.api.dto.PmsProjectInfoDto;
import net.atayun.bazooka.pms.api.dto.ProjectCountDto;
import net.atayun.bazooka.pms.api.dto.ProjectInfoDto;
import net.atayun.bazooka.pms.biz.dal.entity.PmsProjectInfoEntity;
import com.youyu.common.service.IService;

import java.util.List;

/**
 * @author rache
 * @date 2019-07-25
 */
public interface PmsProjectInfoService extends IService<PmsProjectInfoDto, PmsProjectInfoEntity> {
    /**
     * 根据项目id获取项目信息
     * @param id
     * @return
     */

     ProjectInfoDto queryProjectById(long id);

    Integer countProjectInfo(String projectName, String projectCode,Long projectId);
    List<ProjectCountDto> queryProjectCount(String keyWord);
    Integer deleteProject(Long projectId);
    List<PmsProjectInfoDto> queryProjectListForAdmin(Long userId);
}
