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
package net.atayun.bazooka.base.gitlab;

import net.atayun.bazooka.base.git.GitServiceHelp;
import net.atayun.bazooka.base.git.JGitHelpImpl;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.*;
import org.junit.Test;

import java.util.*;

/**
 * @author rache
 * @date 2019-07-15
 */
@Slf4j
public class GitlabUserServiceTest {

    String url = "http://gitlab.gs.9188.com";
    //String token="6jsgsNVg5V7bBcu3uvf3";
    String token="LrtomBLmykAyuqxxXWMx";
    String userNmae="root";
    String pwd="atacloud";
    int groupId=6;

    @Test
    public void test1() {
        try {
            GitLabApi api= GitLabApi.oauth2Login(url,userNmae,pwd);
            List<User> users= api.getUserApi().getActiveUsers();
            System.out.println(users.toString());
        } catch (Exception e)
        {
            log.error("gitlab获取用户异常",e);
        }
    }

    @Test
    public void addGroupTest() {
        try {
            GitLabApi api=new GitLabApi(GitLabApi.ApiVersion.V4,url,token);
            Group newgroup=new Group();
           newgroup.setPath("ata.test");
           newgroup.setName("ata.test");
           newgroup.setVisibility(Visibility.PRIVATE);
           newgroup.setLfsEnabled(true);
           newgroup.setRequestAccessEnabled(true);
            Group group= api.getGroupApi().addGroup(newgroup);
            System.out.println(group.toString());
        } catch (Exception e)
        {
            log.error("gitlab获取用户异常",e);
        }
    }

    @Test
    public void deleteGroupTest() {
        try {
            GitLabApi api=new GitLabApi(GitLabApi.ApiVersion.V4,url,token);
            Integer id=873;
            api.getGroupApi().deleteGroup(id);
            //System.out.println(group.toString());
        } catch (Exception e)
        {
            log.error("gitlab获取用户异常",e);
        }
    }
    @Test
    public void addGroupMemberTest() {
        try {
            GitLabApi api=new GitLabApi(GitLabApi.ApiVersion.V4,url,token);
            Integer id=881;
            Member member= api.getGroupApi().addMember(6,5, AccessLevel.MASTER);
            System.out.println(member.toString());
        } catch (Exception e)
        {
            log.error("gitlab获取用户异常",e);
        }
    }
    @Test
    public void selectGroupTest() {
        try {
            GitLabApi api=new GitLabApi(GitLabApi.ApiVersion.V4,url,token);
            Integer id=873;
            Group group= api.getGroupApi().getGroup(id);
            System.out.println(group.toString());
        } catch (Exception e)
        {
            log.error("gitlab获取用户异常",e);
        }
    }
    @Test
    public void createUserTest() {
        try {
            GitLabApi api=new GitLabApi(GitLabApi.ApiVersion.V4,url,token);
            User user=new User();
            user.setName("赵越铭");
            user.setUsername("zhaoyueming");
            user.setEmail("zhaoyueming@yofish.com");
            user.setSkipConfirmation(true);
            User createUserResult= api.getUserApi().createUser(user,"A123456@",false);
            System.out.println(createUserResult.toString());
        } catch (Exception e)
        {
            log.error("gitlab获取用户异常",e);
        }
    }
    @Test
    public void deleteUserTest() {
        try {
            GitLabApi api=new GitLabApi(GitLabApi.ApiVersion.V4,url,token);
            api.getUserApi().deleteUser(345);
            //System.out.println(createUserResult.toString());
        } catch (Exception e)
        {
            log.error("gitlab获取用户异常",e);
        }
    }
    @Test
    public void selectUserTest() {
        try {
            GitLabApi api=new GitLabApi(GitLabApi.ApiVersion.V4,url,token);
            User user= api.getUserApi().getUser(229);
            System.out.println(user.toString());
        } catch (Exception e) {
            log.error("gitlab获取用户异常",e);
        }
    }
    @Test
    public void createProject() {
        try {
            GitLabApi api=new GitLabApi(GitLabApi.ApiVersion.V4,url,token);
            Project project= api.getProjectApi().createProject(groupId,"common1");
            System.out.println(project.toString());
        } catch (Exception e) {
            log.error("gitlab获取用户异常",e);
        }
    }
    @Test
    public void deleteProject() {
        try {
            GitLabApi api=new GitLabApi(GitLabApi.ApiVersion.V4,url,token);
            api.getProjectApi().deleteProject(1336);
            //System.out.println(project.toString());
        } catch (Exception e) {
            log.error("gitlab获取用户异常",e);
        }
    }
    @Test
    public void selectProject() {
        try {
            GitLabApi api=new GitLabApi(GitLabApi.ApiVersion.V4,url,token);
            Project project= api.getProjectApi().getProject(1336);
            System.out.println(project.toString());
        } catch (Exception e) {
            log.error("gitlab获取用户异常",e);
        }
    }
    @Test
    public void addProjectMember() {
        try {
            GitLabApi api=new GitLabApi(GitLabApi.ApiVersion.V4,url,token);
            Member member= api.getProjectApi().addMember(881,287,AccessLevel.MASTER);
            System.out.println(member.toString());
        } catch (Exception e) {
            log.error("gitlab获取用户异常",e);
        }
    }

    @Test
    public void getloopTest() {
        try {
            GitLabApi gitLabApi=new GitLabApi(GitLabApi.ApiVersion.V4,url,token);
            List<Branch> branchs= gitLabApi.getRepositoryApi().getBranches(1318);
            System.out.println(branchs.toString());
        } catch (Exception e) {
            log.error("gitlab获取用户异常",e);
        }
    }
    @Test
    public void removeProject() {
        try {
            GitLabApi gitLabApi=new GitLabApi(GitLabApi.ApiVersion.V4,url,token);
             gitLabApi.getProjectApi().removeMember(1339,287);
            //System.out.println(branchs.toString());
        } catch (Exception e) {
            log.error("gitlab获取用户异常",e);
        }
    }
    @Test
    public void git() {
        String url="https://github.com/alibaba/fastjson.git";
        GitServiceHelp gitServiceHelp=new JGitHelpImpl();
        Set<String> list= gitServiceHelp.branchList(url,"sdada","");
        System.out.println(list.toString());
    }



}
