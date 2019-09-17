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
package net.atayun.bazooka.pms.biz.service.impl;

import net.atayun.bazooka.pms.api.enums.UserTypeEnum;
import net.atayun.bazooka.pms.biz.service.ProjectExtra;
import org.gitlab4j.api.models.User;

/**
 * 自定义git外部库实现
 * @author rache
 * @date 2019-08-26
 */
public class ProjectExtraGitImpl implements ProjectExtra {
    @Override
    public Integer createProject(String projectName) {
        return null;
    }

    @Override
    public void addProjectMember(Integer groupId, Integer gitLabUserId, UserTypeEnum roleType) {

    }

    @Override
    public void updateProjectMember(Integer groupId, Integer gitLabUserId, UserTypeEnum roleType) {

    }

    @Override
    public void deleteProjectMember(Integer groupId, Integer gitLabUserId) {

    }

    @Override
    public void deleteProject(Integer projectId) {

    }

    @Override
    public User createUser(String email, String userName, String Pwd) {
        return null;
    }
}
