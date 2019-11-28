package net.atayun.bazooka.deploy.biz.v2.util;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ping
 */
public class MessageDesensitizationUtil {

    private static final Pattern DOCKER_RUN_PATTERN = Pattern.compile("-u\\s+(.+?)\\s+-p\\s+(.+?)\\s");
    private static final Pattern GIT_CLONE_PATTERN = Pattern.compile(":\\/\\/(.+?)@");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("USERNAME=(.+?),");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("PASSWORD=(.+?),");
    private static final String REPLACEMENT = "******";

    public static String replaceDockerCmd(String cmd) {
        if (StringUtils.isEmpty(cmd)) {
            return "";
        }
        String finalStr = cmd;
        Matcher matcher = DOCKER_RUN_PATTERN.matcher(cmd);
        if (matcher.find()) {
            String username = matcher.group(1);
            if (StringUtils.hasText(username)) {
                finalStr = finalStr.replace(username, REPLACEMENT);
            }
            String password = matcher.group(2);
            if (StringUtils.hasText(password)) {
                finalStr = finalStr.replace(password, REPLACEMENT);
            }
        }
        return finalStr;
    }

    public static String replaceGitCloneCmd(String gitCloneCmd) {
        return replaceOne(GIT_CLONE_PATTERN, gitCloneCmd);
    }

    public static String replaceUsernameE(String username) {
        return replaceOne(USERNAME_PATTERN, username);
    }

    public static String replacePasswordE(String password) {
        return replaceOne(PASSWORD_PATTERN, password);
    }

    public static String replaceOne(Pattern pattern, String str) {
        if (StringUtils.isEmpty(str)) {
            return "";
        }
        String finalStr = str;
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            String privacy = matcher.group(1);
            if (StringUtils.hasText(privacy)) {
                finalStr = finalStr.replace(privacy, REPLACEMENT);
            }
        }
        return finalStr;
    }

    public static String jenkins(String string) {
        String log = MessageDesensitizationUtil.replaceGitCloneCmd(string);
        log = MessageDesensitizationUtil.replaceDockerCmd(log);
        log = MessageDesensitizationUtil.replaceUsernameE(log);
        return MessageDesensitizationUtil.replacePasswordE(log);
    }
}
