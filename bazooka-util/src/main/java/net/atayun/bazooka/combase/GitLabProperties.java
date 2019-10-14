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
package net.atayun.bazooka.combase;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author rache
 * @date 2019-08-26
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "ata.gitlab")
public class GitLabProperties {
    @NotEmpty
    private Boolean ataGitlab;
    @NotEmpty
    private String privateToken;
    @NotEmpty
    private String userName;
    @NotEmpty
    private String passWord;

}
