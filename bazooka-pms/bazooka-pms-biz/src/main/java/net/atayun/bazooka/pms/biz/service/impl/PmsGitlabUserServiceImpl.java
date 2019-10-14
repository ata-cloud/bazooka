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

import net.atayun.bazooka.pms.api.dto.PmsGitlabUserDto;
import net.atayun.bazooka.pms.biz.dal.dao.PmsGitlabUserMapper;
import net.atayun.bazooka.pms.biz.dal.entity.PmsGitlabUserEntity;
import net.atayun.bazooka.pms.biz.service.PmsGitlabUserService;
import net.atayun.bazooka.pms.biz.service.ProjectExtra;
import com.youyu.common.service.AbstractService;
import org.gitlab4j.api.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author rache
 * @date 2019-07-25
 */
@Service
public class PmsGitlabUserServiceImpl extends AbstractService<Long, PmsGitlabUserDto, PmsGitlabUserEntity, PmsGitlabUserMapper> implements PmsGitlabUserService   {

    @Autowired
    private ProjectExtra projectExtra;
    @Override
    public void addGitlabUser(Long userId, String email, String userName) {
        if (checkUser(userId)) {
            return;
        }
        String pwd="F123456@";
        User user= projectExtra.createUser(email,userName,pwd);
        PmsGitlabUserEntity entity=new PmsGitlabUserEntity();
        entity.setUserId(userId);
        entity.setGitlabUserId(user.getId());
        super.insert(entity);
    }

    private boolean checkUser(Long userId){
        PmsGitlabUserEntity entity=new PmsGitlabUserEntity();
        entity.setUserId(userId);
        List<PmsGitlabUserDto> pmsGitlabUserDto= super.select(entity);
        if(pmsGitlabUserDto!=null&&pmsGitlabUserDto.size()==1){
            return true;
        }
        return false;
    }
    @Override
    public PmsGitlabUserEntity getUserInfo(Long userId) {
        PmsGitlabUserEntity entity=new PmsGitlabUserEntity();
        entity.setUserId(userId);
        /*PmsGitlabUserEntity result= super.selectOneEntity(domain);
        if(result==null){
            throw new BizException(PMS_NO_GITLAB_USER.getCode(),PMS_NO_GITLAB_USER.getDesc());
        }*/
        entity.setGitlabUserId(0);
        return entity;
    }
}
