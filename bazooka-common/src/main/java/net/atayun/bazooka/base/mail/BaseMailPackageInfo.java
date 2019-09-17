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


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 邮件包基类
 *
 * @author WangSongJun
 * @date 2018-09-29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseMailPackageInfo {
    @ApiModelProperty("发件人")
    @NotNull(message = "发件人邮箱地址不能为空")
    private MailAddress fromUser;

    @ApiModelProperty("密码")
    @NotBlank(message = "邮箱密码不能为空")
    private String password;

    @ApiModelProperty("邮箱服务器地址")
    @NotBlank(message = "邮箱服务器地址不能为空")
    private String host;

    @ApiModelProperty("收件人")
    @NotEmpty(message = "收件人邮箱地址不能为空")
    private MailAddress[] toUser;

    @ApiModelProperty("主题")
    @NotBlank(message = "邮件主题不能为空")
    private String subject;
    @ApiModelProperty("内容")
    @NotBlank(message = "邮箱内容不能为空")
    private String contents;
}
