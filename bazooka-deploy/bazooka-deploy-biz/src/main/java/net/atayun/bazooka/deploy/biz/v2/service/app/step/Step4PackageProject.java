package net.atayun.bazooka.deploy.biz.v2.service.app.step;

import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.base.constant.FlowStepConstants;
import net.atayun.bazooka.deploy.biz.v2.constant.JenkinsBuildJobConstants;
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
public class Step4PackageProject extends Step4Jenkins {

    @Override
    protected Map<String, String> getJobParam(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        AppDeployConfigDto appDeployConfig = getBean(AppApi.class).getAppDeployConfigInfoById(appOpt.getDeployConfigId())
                .ifNotSuccessThrowException().getData();

        Map<String, String> param = new HashMap<>();
        param.put(JenkinsBuildJobConstants.OPT_ID, appOpt.getId().toString());
        param.put(JenkinsBuildJobConstants.STEP_ID, appOptFlowStep.getId().toString());
        param.put(JenkinsBuildJobConstants.WORK_PATH, (String) appOptFlowStep.getInput().get("workPath"));
        param.put(JenkinsBuildJobConstants.COMPILE_COMMAND, appDeployConfig.getCompileCommand());
        param.put(JenkinsBuildJobConstants.BUILD_CALLBACK_URI, jenkinsJobPropertiesHelper.buildCallbackUri());
        return param;
    }

    @Override
    protected String getJobName() {
        return this.jenkinsJobPropertiesHelper.randomPackageProject();
    }
}
