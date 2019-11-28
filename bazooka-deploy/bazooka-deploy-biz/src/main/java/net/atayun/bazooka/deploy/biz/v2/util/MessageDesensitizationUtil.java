package net.atayun.bazooka.deploy.biz.v2.util;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ping
 */
public class MessageDesensitizationUtil {

    private static final Pattern COMPILE = Pattern.compile("-u\\s+(\\w+)\\s+-p\\s+(\\w+)");

    public static String dockerCmd(String cmd) {
        String finalStr = cmd;
        Matcher matcher = COMPILE.matcher(cmd);
        if (matcher.find()) {
            String username = matcher.group(1);
            if (StringUtils.hasText(username)) {
                finalStr = finalStr.replace(username, "******");
            }
            String password = matcher.group(2);
            if (StringUtils.hasText(password)) {
                finalStr = finalStr.replace(password, "******");
            }
        }
        return finalStr;
    }
}
