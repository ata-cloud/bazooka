package net.atayun.bazooka.deploy.biz.v2.service.app.step.jenkins;

import net.atayun.bazooka.base.jenkins.JenkinsJobPropertiesHelper;
import net.atayun.bazooka.base.jenkins.JenkinsServerHelper;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.Step;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author Ping
 */
public abstract class Step4Jenkins extends Step {

    @Autowired
    private JenkinsServerHelper jenkinsServerHelper;

    @Autowired
    protected JenkinsJobPropertiesHelper jenkinsJobPropertiesHelper;

    @Override
    public void doWork(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        this.jenkinsServerHelper.buildJob(getJobName(), getJobParam(appOpt, appOptFlowStep));
    }

    protected abstract Map<String, String> getJobParam(AppOpt appOpt, AppOptFlowStep appOptFlowStep);

    protected abstract String getJobName();
}