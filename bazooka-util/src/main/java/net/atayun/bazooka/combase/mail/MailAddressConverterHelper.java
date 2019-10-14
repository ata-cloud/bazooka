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

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * @author WangSongJun
 * @date 2018-10-08
 */
public class MailAddressConverterHelper {

    public static InternetAddress convert(MailAddress mailAddress) {
        try {
            return new InternetAddress(mailAddress.getAddress(), mailAddress.getPersonal());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static InternetAddress[] convert(MailAddress[] mailAddresses) {
        return Arrays.stream(mailAddresses).map(MailAddressConverterHelper::convert).toArray(InternetAddress[]::new);
    }

    public static String addressToString(MailAddress[] mailAddresses) {
        return Arrays.toString(Arrays.stream(mailAddresses).map(MailAddress::getAddress).toArray());
    }

}
