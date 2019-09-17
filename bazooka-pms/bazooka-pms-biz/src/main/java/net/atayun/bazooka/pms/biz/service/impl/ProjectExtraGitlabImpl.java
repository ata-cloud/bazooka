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
import com.youyu.common.exception.BizException;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.*;
import org.springframework.beans.factory.annotation.Autowired;

import static net.atayun.bazooka.pms.api.enums.PmsResultCode.*;

/**
 * @author rache
 * @date 2019-07-16
 */

public class ProjectExtraGitlabImpl implements ProjectExtra {
    @Autowired
    private GitLabApi gitLabApi;
    @Override
    public Integer createProject(String projectName) {
        Group newGroup=new Group();
        newGroup.setPath(projectName);
        newGroup.setName(projectName);
        newGroup.setVisibility(Visibility.PRIVATE);
        newGroup.setLfsEnabled(true);
        newGroup.setRequestAccessEnabled(true);
        try {
            Group group = gitLabApi.getGroupApi().addGroup(newGroup);
            return  group.getId();
        }
        catch (Exception e) {
            throw new BizException(GITLAB_CREATE_ERROR.getCode(),GITLAB_CREATE_ERROR.getDesc(),e);
        }

    }

    @Override
    public void addProjectMember(Integer groupId, Integer gitLabUserId, UserTypeEnum roleType) {
        try {
            AccessLevel level=null;
            switch (roleType){
                case USER_PROJECT_DEV:
                    level=AccessLevel.DEVELOPER;
                    break;
                case USER_PROJECT_MASTER:
                    level=AccessLevel.MAINTAINER;
                    break;
            }
            gitLabApi.getGroupApi().addMember(groupId,gitLabUserId, level);
        }
        catch (Exception e) {
            throw new BizException(GITLAB_MEMBER_ADD_ERROR.getCode(),GITLAB_MEMBER_ADD_ERROR.getDesc(),e);
        }

    }


    @Override
    public void updateProjectMember(Integer groupId, Integer gitLabUserId, UserTypeEnum roleType) {
        try {
            AccessLevel level=null;
            switch (roleType){
                case USER_PROJECT_DEV:
                    level=AccessLevel.DEVELOPER;
                    break;
                case USER_PROJECT_MASTER:
                    level=AccessLevel.MAINTAINER;
                    break;
            }
            gitLabApi.getGroupApi().updateMember(groupId,gitLabUserId, level);
        }
        catch (Exception e) {
            throw new BizException(GITLAB_MEMBER_ADD_ERROR.getCode(),GITLAB_MEMBER_ADD_ERROR.getDesc(),e);
        }

    }

    @Override
    public void deleteProjectMember(Integer groupId, Integer gitLabUserId) {
        try {

            gitLabApi.getGroupApi().removeMember(groupId,gitLabUserId);
        }
        catch (Exception e) {
            throw new BizException(GITLAB_MEMBER_ADD_ERROR.getCode(),GITLAB_MEMBER_ADD_ERROR.getDesc(),e);
        }

    }

    @Override
    public void deleteProject(Integer groupId) {
        try {
            gitLabApi.getGroupApi().deleteGroup(groupId);
        }
        catch (Exception e) {
            throw new BizException(GITLAB_DELETE_ERROR.getCode(),GITLAB_DELETE_ERROR.getDesc(),e);
        }
    }

    @Override
    public User createUser(String email, String userName, String Pwd) {
        try {
            User user=new User();
            user.setName(userName);
            user.setUsername(userName);
            user.setEmail(email);
            user.setSkipConfirmation(true);
            User user1=  gitLabApi.getUserApi().createUser(user,Pwd,false);
            return user1;
        }
        catch (Exception e) {
            throw new BizException(GITLAB_MEMBER_ADD_ERROR.getCode(),GITLAB_MEMBER_ADD_ERROR.getDesc(),e);
        }
    }
}
