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
package net.atayun.bazooka.upms.biz.dal.entity;

import net.atayun.bazooka.upms.api.dto.req.UserAddReqDTO;
import net.atayun.bazooka.upms.api.dto.req.UserEditReqDTO;
import net.atayun.bazooka.upms.api.dto.req.UserModifyPasswordReqDTO;
import net.atayun.bazooka.upms.biz.component.strategy.shiro.signature.ShiroSimpleHashStrategy;
import com.youyu.common.entity.JdbcMysqlEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Table;

import static net.atayun.bazooka.base.bean.StrategyNumBean.getBeanInstance;
import static net.atayun.bazooka.base.utils.BizExceptionUtil.exception;
import static net.atayun.bazooka.base.utils.BizExceptionUtil.exception2MatchingExpression;
import static net.atayun.bazooka.base.utils.StringUtil.eq;
import static net.atayun.bazooka.upms.api.enums.UpmsResultCode.USER_STATUS_INVALID;
import static net.atayun.bazooka.upms.api.enums.UpmsResultCode.WRONG_PASSWORD;
import static net.atayun.bazooka.upms.api.enums.UserStatusEnum.INVALID;
import static net.atayun.bazooka.upms.api.enums.UserStatusEnum.VALID;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 用户
 */
@Setter
@Getter
@Table(name = "upms_user")
public class User extends JdbcMysqlEntity<Long> {

    /**
     * 用户名
     */
    @Column(name = "username")
    private String username;

    /**
     * 密码
     */
    @Column(name = "password")
    private String password;

    /**
     * 真实姓名
     */
    @Column(name = "real_name")
    private String realName;

    /**
     * 性别(0:男 1:女 2:其他)
     */
    @Column(name = "sex")
    private Integer sex;

    /**
     * 手机号
     */
    @Column(name = "phone")
    private String phone;

    /**
     * 邮箱
     */
    @Column(name = "email")
    private String email;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 状态(0:有效 1:冻结)
     */
    @Column(name = "status")
    private String status;

    public User() {
    }

    public User(String username) {
        this();
        this.username = username;
    }

    public User(String username, String password) {
        this(username);
        this.password = password;
    }

    public User(UserAddReqDTO userAddReqDTO, String hashAlgorithmName) {
        this(userAddReqDTO.getUsername(), getBeanInstance(ShiroSimpleHashStrategy.class, hashAlgorithmName).signature(userAddReqDTO.getPassword()));
        this.realName = userAddReqDTO.getRealName();
        this.sex = userAddReqDTO.getSex();
        this.phone = userAddReqDTO.getPhone();
        this.email = userAddReqDTO.getEmail();
        this.status = defaultIfBlank(userAddReqDTO.getStatus(), VALID.getCode());
        this.remark = userAddReqDTO.getRemark();
    }

    public User(UserEditReqDTO userEditReqDTO, String hashAlgorithmName) {
        this();
        setId(userEditReqDTO.getUserId());
        this.realName = userEditReqDTO.getRealName();
        this.sex = userEditReqDTO.getSex();
        this.phone = userEditReqDTO.getPhone();
        this.email = userEditReqDTO.getEmail();
        this.status = defaultIfBlank(userEditReqDTO.getStatus(), VALID.getCode());
        this.remark = userEditReqDTO.getRemark();
        if (isNoneBlank(userEditReqDTO.getPassword())) {
            this.password = getBeanInstance(ShiroSimpleHashStrategy.class, hashAlgorithmName).signature(userEditReqDTO.getPassword());
        }
    }

    /**
     * 检查用户状态
     */
    public void checkStatus() {
        exception2MatchingExpression(eq(status, INVALID.getCode()), USER_STATUS_INVALID);
    }

    /**
     * 修改密码
     *
     * @param userModifyPasswordReqDTO
     * @param hashAlgorithmName
     */
    public void modifyPassword(UserModifyPasswordReqDTO userModifyPasswordReqDTO, String hashAlgorithmName) {
        checkPassword(userModifyPasswordReqDTO.getOriginPassword(), hashAlgorithmName);
        this.password = signature(userModifyPasswordReqDTO.getPassword(), hashAlgorithmName);
    }

    /**
     * 检查用户输入的密码是否合法
     *
     * @param originPassword
     * @param hashAlgorithmName
     */
    private void checkPassword(String originPassword, String hashAlgorithmName) {
        String password = signature(originPassword, hashAlgorithmName);
        if (!eq(this.password, password)) {
            exception(WRONG_PASSWORD);
        }
    }

    /**
     * 散列原始密码
     *
     * @param password
     * @param hashAlgorithmName
     * @return
     */
    private String signature(String password, String hashAlgorithmName) {
        return getBeanInstance(ShiroSimpleHashStrategy.class, hashAlgorithmName).signature(password);
    }

    /**
     * 用户id
     *
     * @return
     */
    public Long getUserId() {
        return getId();
    }
}
