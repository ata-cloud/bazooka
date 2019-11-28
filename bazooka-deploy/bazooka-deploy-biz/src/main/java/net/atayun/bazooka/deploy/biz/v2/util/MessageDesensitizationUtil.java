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
        String[] cmdArr = cmd.split("&&");
        if (cmdArr.length > 1) {
            String loginCmd = cmdArr[0];
            Matcher matcher = COMPILE.matcher(loginCmd);
            if (matcher.find()) {
                String username = matcher.group(1);
                if (StringUtils.hasText(username)) {
                    loginCmd = loginCmd.replace(username, "******");
                }
                String password = matcher.group(2);
                if (StringUtils.hasText(password)) {
                    loginCmd = loginCmd.replace(password, "******");
                }
                cmdArr[0] = loginCmd;
            }
        }
        return String.join("&&", cmdArr);
    }
}
