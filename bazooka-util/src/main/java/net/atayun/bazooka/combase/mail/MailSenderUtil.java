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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 邮件服务抽象接口实现
 *
 * @author WangSongJun
 * @date 2018-09-29
 */
@Slf4j
@Service
public class MailSenderUtil {
    @Value("${java.mail.timeout:6000}")
    private Integer timeout;

    /**
     * 发送邮件
     *
     * @param mailPackage
     */
    public boolean send(MailPackageInfoParam mailPackage) {
        try {
            //1.创建邮件发送服务器
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost(mailPackage.getHost());
            mailSender.setUsername(mailPackage.getFromUser().getAddress());
            mailSender.setPassword(mailPackage.getPassword());
            mailSender.setDefaultEncoding(mailPackage.getEncoding());

            //2.加认证机制
            Properties javaMailProperties = new Properties();
            javaMailProperties.put("mail.smtp.auth", true);
            javaMailProperties.put("mail.smtp.starttls.enable", true);
            javaMailProperties.put("mail.smtp.timeout", timeout);
            mailSender.setJavaMailProperties(javaMailProperties);

            //3.创建邮件内容
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setFrom(MailAddressConverterHelper.convert(mailPackage.getFromUser()));
            messageHelper.setTo(MailAddressConverterHelper.convert(mailPackage.getToUser()));
            if (null != mailPackage.getReplyTo()) {
                messageHelper.setReplyTo(MailAddressConverterHelper.convert(mailPackage.getReplyTo()));
            }
            if (null != mailPackage.getCarbonCopy()) {
                messageHelper.setCc(MailAddressConverterHelper.convert(mailPackage.getCarbonCopy()));
            }
            if (null != mailPackage.getBlindCarbonCopy()) {
                messageHelper.setBcc(MailAddressConverterHelper.convert(mailPackage.getBlindCarbonCopy()));
            }
            //附件
            MultipartFile[] attachments = mailPackage.getAttachments();
            if (!ObjectUtils.isEmpty(attachments)) {
                for (MultipartFile attachment : attachments) {
                    messageHelper.addAttachment(attachment.getOriginalFilename(), attachment);
                }
            }
            messageHelper.setSubject(mailPackage.getSubject());
            messageHelper.setText(mailPackage.getContents(), mailPackage.getIsHtml());

            //4.发送邮件
            mailSender.send(mimeMessage);
            log.info("邮件发送成功。");
            return true;
        } catch (Exception e) {
            log.warn("邮件发送失败", e);
            return false;
        }
    }

}
