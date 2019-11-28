package net.atayun.bazooka.deploy.biz.v2.service.app.step;

import com.youyu.common.exception.BizException;
import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.constant.FlowStepConstants;
import net.atayun.bazooka.deploy.biz.v2.constant.DeployResultCodeConstants;
import net.atayun.bazooka.deploy.biz.v2.constant.JenkinsBuildJobConstants;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.jenkins.Step4Jenkins;
import net.atayun.bazooka.pms.api.dto.AppInfoWithCredential;
import net.atayun.bazooka.pms.api.feign.AppApi;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = Step.class, number = FlowStepConstants.PULL_CODE)
public class Step4PullCode extends Step4Jenkins {

    @Override
    protected Map<String, String> getJobParam(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        AppInfoWithCredential appInfo = getBean(AppApi.class).getAppInfoWithCredentialById(appOpt.getAppId())
                .ifNotSuccessThrowException().getData();

        Map<String, String> param = new HashMap<>();
        param.put(JenkinsBuildJobConstants.OPT_ID, appOpt.getId().toString());
        param.put(JenkinsBuildJobConstants.STEP_ID, appOptFlowStep.getId().toString());
        param.put(JenkinsBuildJobConstants.SERVICE_NAME, appInfo.getAppCode());
        param.put(JenkinsBuildJobConstants.BRANCH, appOpt.getBranch());
        param.put(JenkinsBuildJobConstants.GIT_REPOSITORY_URL, addUserInfo(appInfo.getGitlabUrl(), appInfo));
        param.put(JenkinsBuildJobConstants.BUILD_CALLBACK_URI, jenkinsJobPropertiesHelper.buildCallbackUri());
        return param;
    }

    @Override
    protected String getJobName() {
        return this.jenkinsJobPropertiesHelper.randomPullCode();
    }

    private String addUserInfo(String uriStr, AppInfoWithCredential appInfo) {
        try {
            String userInfo = appInfo.getCredentialKey() + ":" + appInfo.getCredentialValue();
            URI uri = new URI(uriStr);
            return new URI(uri.getScheme(), userInfo, uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(),
                    uri.getFragment()).toString();
        } catch (Exception e) {
            throw new BizException(DeployResultCodeConstants.GIT_URI_ERROR, e);
        }
    }
}
