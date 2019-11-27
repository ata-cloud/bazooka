package net.atayun.bazooka.deploy.biz.v2.service.app.step.deploymode;

import com.youyu.common.exception.BizException;
import net.atayun.bazooka.base.git.GitServiceHelp;
import net.atayun.bazooka.deploy.biz.v2.constant.DeployResultCodeConstants;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.dto.AppInfoWithCredential;
import net.atayun.bazooka.pms.api.feign.AppApi;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Set;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
public interface ICheckBranch {

    default void checkBranch(AppOpt appOpt) {
        AppApi appApi = getBean(AppApi.class);
        AppDeployConfigDto appDeployConfigDto = appApi.getAppDeployConfigInfoById(appOpt.getDeployConfigId())
                .ifNotSuccessThrowException().getData();

        String branch = appOpt.getBranch();
        String gitBranchAllow = appDeployConfigDto.getGitBranchAllow();
        String gitBranchDeny = appDeployConfigDto.getGitBranchDeny();

        if (StringUtils.isEmpty(gitBranchAllow) && StringUtils.isEmpty(gitBranchDeny)) {
            //log
        } else {
            if (StringUtils.hasText(gitBranchAllow)) {
                gitBranchAllow = gitBranchAllow.replace("*", "");
                if (!branch.contains(gitBranchAllow)) {
                    throw new BizException(DeployResultCodeConstants.ILLEGAL_BRANCH, String.format("分支无效[只允许以%s为前缀的分支]", gitBranchAllow));
                }
            } else {
                if (StringUtils.hasText(gitBranchDeny)) {
                    gitBranchDeny = gitBranchDeny.replace("*", "");
                    if (branch.contains(gitBranchDeny)) {
                        throw new BizException(DeployResultCodeConstants.ILLEGAL_BRANCH, String.format("分支无效[不允许以%s为前缀的分支]", gitBranchDeny));
                    }
                } else {
                    throw new BizException(DeployResultCodeConstants.ILLEGAL_BRANCH, "未设置分支约束");
                }
            }
        }
        AppInfoWithCredential appInfo = appApi.getAppInfoWithCredentialById(appOpt.getAppId()).ifNotSuccessThrowException().getData();
        Set<String> branchSet = getBean(GitServiceHelp.class).branchList(appInfo.getGitlabUrl(), appInfo.getCredentialKey(), appInfo.getCredentialValue());
        branchSet.stream()
                .filter(bch -> Objects.equals(bch, branch))
                .findAny()
                .orElseThrow(() -> new BizException(DeployResultCodeConstants.ILLEGAL_BRANCH, String.format("分支[%s]不存在", branch)));
    }
}
