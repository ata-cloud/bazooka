package net.atayun.bazooka.pms.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/9 下午5:25
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Credentials {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * 凭据域
     */
//    @Column(name="domain")
//    private CredentialDomainEnum domain;
//    /**
//     * 使用范围
//     */
//    @Column(name="scope")
//    private CredentialScopeEnum scope;
//    /**
//     * 凭据名称
//     */
//    private String credentialName;
    /**
     * 凭据类型
     */
//    @Column(name = "credential_type")
//    private CredentialTypeEnum credentialType;
    /**
     * 凭据key
     */
    private String credentialKey;
    /**
     * 凭据value
     */
    private String credentialValue;
}
