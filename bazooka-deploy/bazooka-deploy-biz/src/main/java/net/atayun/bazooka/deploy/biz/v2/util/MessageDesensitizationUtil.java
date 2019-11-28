package net.atayun.bazooka.deploy.biz.v2.util;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ping
 */
public class MessageDesensitizationUtil {

    private static final Pattern DOCKER_RUN_PATTERN = Pattern.compile("-u\\s+(\\w+)\\s+-p\\s+(\\w+)");
    private static final Pattern GIT_CLONE_PATTERN = Pattern.compile(":\\/\\/(.+?)@");
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
        if (StringUtils.isEmpty(gitCloneCmd)) {
            return "";
        }
        String finalStr = gitCloneCmd;
        Matcher matcher = GIT_CLONE_PATTERN.matcher(gitCloneCmd);
        if (matcher.find()) {
            String privacy = matcher.group(1);
            if (StringUtils.hasText(privacy)) {
                finalStr = finalStr.replace(privacy, REPLACEMENT);
            }
        }
        return finalStr;
    }
}
