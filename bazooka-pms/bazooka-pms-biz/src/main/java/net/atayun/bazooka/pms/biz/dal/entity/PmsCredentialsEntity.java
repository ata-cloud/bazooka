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
package net.atayun.bazooka.pms.biz.dal.entity;

import net.atayun.bazooka.pms.api.enums.CredentialDomainEnum;
import net.atayun.bazooka.pms.api.enums.CredentialScopeEnum;
import net.atayun.bazooka.pms.api.enums.CredentialTypeEnum;
import com.youyu.common.entity.JdbcMysqlEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 凭据管理
 *
 * @author WangSongJun
 * @date 2019-08-27
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pms_credentials")
public class PmsCredentialsEntity extends JdbcMysqlEntity<Long> {
    public PmsCredentialsEntity(CredentialDomainEnum domain) {
        this.domain = domain;
    }

    public PmsCredentialsEntity(String credentialName) {
        this.credentialName = credentialName;
    }

    public PmsCredentialsEntity(Long id, String credentialKey, String credentialValue) {
        super(id);
        this.credentialKey = credentialKey;
        this.credentialValue = credentialValue;
    }

    /**
     * 凭据域
     */
    @Column(name="domain")
    private CredentialDomainEnum domain;
    /**
     * 使用范围
     */
    @Column(name="scope")
    private CredentialScopeEnum scope;
    /**
     * 凭据名称
     */
    private String credentialName;
    /**
     * 凭据类型
     */
    @Column(name="credential_type")
    private CredentialTypeEnum credentialType;
    /**
     * 凭据key
     */
    private String credentialKey;
    /**
     * 凭据value
     */
    private String credentialValue;

}
