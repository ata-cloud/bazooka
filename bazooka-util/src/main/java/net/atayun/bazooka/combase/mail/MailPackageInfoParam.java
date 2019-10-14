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
package net.atayun.bazooka.combase.mail;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * 邮件接口的参数包
 *
 * @author WangSongJun
 * @date 2018-09-29
 */
@Getter
@Setter
@NoArgsConstructor
public class MailPackageInfoParam extends BaseMailPackageInfo {

    @ApiModelProperty("编码格式")
    private String encoding = "UTF-8";

    @ApiModelProperty("是否HTML邮件")
    private Boolean isHtml = false;

    @ApiModelProperty("回复")
    private MailAddress replyTo;

    @ApiModelProperty("抄送")
    private MailAddress[] carbonCopy;

    @ApiModelProperty("密送")
    private MailAddress[] blindCarbonCopy;

    @ApiModelProperty("附件")
    private MultipartFile[] attachments;

    @ApiModelProperty("自定义标签")
    private String customTag;

    /**
     * 发送普通文本邮件
     *
     * @param fromUser
     * @param password
     * @param host
     * @param toUser
     * @param subject
     * @param contents
     */
    public MailPackageInfoParam(MailAddress fromUser, String password, String host, MailAddress[] toUser, String subject, String contents) {
        super(fromUser, password, host, toUser, subject, contents);
    }

    /**
     * 发送普通文本邮件并自定义相关设置
     *
     * @param fromUser
     * @param password
     * @param host
     * @param toUser
     * @param subject
     * @param contents
     */
    public MailPackageInfoParam(MailAddress fromUser, String password, String host, MailAddress[] toUser, String subject, String contents, String encoding, Boolean isHtml) {
        super(fromUser, password, host, toUser, subject, contents);
        this.encoding = encoding;
        this.isHtml = isHtml;
    }

    /**
     * @param fromUser
     * @param password
     * @param host
     * @param toUser
     * @param subject
     * @param contents
     * @param encoding
     * @param isHtml
     * @param carbonCopy
     * @param customTag
     */
    public MailPackageInfoParam(MailAddress fromUser, String password, String host, MailAddress[] toUser, String subject, String contents, String encoding, Boolean isHtml, MailAddress[] carbonCopy, String customTag) {
        super(fromUser, password, host, toUser, subject, contents);
        this.encoding = encoding;
        this.isHtml = isHtml;
        this.carbonCopy = carbonCopy;
        this.customTag = customTag;
    }

    /**
     * 添加抄送、密送、附件、自定义标签相关
     *
     * @param fromUser
     * @param password
     * @param host
     * @param toUser
     * @param subject
     * @param contents
     * @param encoding
     * @param isHtml
     * @param carbonCopy
     * @param customTag
     * @param replyTo
     * @param blindCarbonCopy
     * @param attachments
     */
    public MailPackageInfoParam(MailAddress fromUser, String password, String host, MailAddress[] toUser, String subject, String contents, String encoding, Boolean isHtml, MailAddress[] carbonCopy, String customTag, MailAddress replyTo, MailAddress[] blindCarbonCopy, MultipartFile[] attachments) {
        super(fromUser, password, host, toUser, subject, contents);
        this.encoding = encoding;
        this.isHtml = isHtml;
        this.replyTo = replyTo;
        this.carbonCopy = carbonCopy;
        this.blindCarbonCopy = blindCarbonCopy;
        this.attachments = attachments;
        this.customTag = customTag;
    }

}
