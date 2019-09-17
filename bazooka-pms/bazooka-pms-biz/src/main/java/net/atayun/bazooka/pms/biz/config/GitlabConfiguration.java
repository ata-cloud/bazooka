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
package net.atayun.bazooka.pms.biz.config;

import net.atayun.bazooka.base.config.GitLabProperties;
import net.atayun.bazooka.base.dcos.DcosPropertiesHelper;
import net.atayun.bazooka.base.git.GitServiceHelp;
import net.atayun.bazooka.base.git.JGitHelpImpl;
import net.atayun.bazooka.pms.biz.service.ProjectExtra;
import net.atayun.bazooka.pms.biz.service.impl.ProjectExtraAtaImpl;
import net.atayun.bazooka.pms.biz.service.impl.ProjectExtraGitImpl;
import com.youyu.common.exception.BizException;
import org.gitlab4j.api.GitLabApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author rache
 * @date 2019-07-25
 */
@Configuration
@EnableConfigurationProperties({
        GitLabProperties.class
})
public class GitlabConfiguration {


    @Bean
    @ConditionalOnProperty(prefix ="ata.gitlab" ,name = "ata-gitlab",havingValue = "true")
    public GitLabApi gitLabApi(DcosPropertiesHelper dcos,GitLabProperties gitLabProperties) {

        String url = dcos.dcosPublicAgentIp() + ":7000";
        try {
            return new GitLabApi(GitLabApi.ApiVersion.V4, url, gitLabProperties.getPrivateToken());
            //return  GitLabApi.oauth2Login(url,userNmae,pwd);
        } catch (Exception e) {
            throw new BizException("0001", "gitlab初始化失败");
        }
    }
    @Bean
    public ProjectExtra projectExtra(GitLabProperties gitLabProperties){
        if(gitLabProperties.getAtaGitlab()){
            return new ProjectExtraAtaImpl();
        }else {
            return new ProjectExtraGitImpl();
        }
    }
    @Bean
    public GitServiceHelp gitServiceHelp(){
        GitServiceHelp gitServiceHelp=new JGitHelpImpl();
        return gitServiceHelp;
    }

}
