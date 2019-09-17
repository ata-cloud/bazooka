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
package net.atayun.bazooka.base.mail;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * @author WangSongJun
 * @date 2018-10-08
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("邮件地址")
public class MailAddress {
    public MailAddress(String address) {
        this.address = address;
    }

    /**
     * email address
     */
    @ApiModelProperty("邮箱地址")
    @Email(message = "邮箱格式错误")
    @NotBlank(message = "邮箱地址不能为空")
    private String address;

    /**
     * The personal name.
     */
    @ApiModelProperty("人名")
    private String personal;

}
