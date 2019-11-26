package net.atayun.bazooka.deploy.biz.v2.service.app.step;

import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.deploy.biz.constants.JenkinsBuildJobConstants;
import net.atayun.bazooka.deploy.biz.v2.constant.FlowStepConstants;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.jenkins.Step4Jenkins;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.feign.AppApi;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = Step.class, number = FlowStepConstants.PACKAGE_PROJECT)
public class Step4PackageProject extends Step4Jenkins implements Callback {

    @Override
    public void callback(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        //无特殊处理
    }

    @Override
    protected Map<String, String> getJobParam(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        AppDeployConfigDto appDeployConfig = getBean(AppApi.class).getAppDeployConfigInfoById(appOpt.getDeployConfigId())
                .ifNotSuccessThrowException().getData();

        Map<String, String> param = new HashMap<>();
        param.put(JenkinsBuildJobConstants.OPT_ID, appOpt.getId().toString());
        param.put(JenkinsBuildJobConstants.STEP_ID, appOptFlowStep.getId().toString());
        param.put(JenkinsBuildJobConstants.COMPILE_COMMAND, appDeployConfig.getCompileCommand());
        param.put(JenkinsBuildJobConstants.BUILD_CALLBACK_URI, jenkinsJobPropertiesHelper.buildCallbackUri());
        return param;
    }

    @Override
    protected String getJobName() {
        return this.jenkinsJobPropertiesHelper.randomPackageProject();
    }
}
