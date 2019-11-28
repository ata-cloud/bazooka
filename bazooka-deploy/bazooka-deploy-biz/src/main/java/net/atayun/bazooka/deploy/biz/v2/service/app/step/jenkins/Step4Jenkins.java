package net.atayun.bazooka.deploy.biz.v2.service.app.step.jenkins;

import net.atayun.bazooka.base.jenkins.JenkinsJobPropertiesHelper;
import net.atayun.bazooka.base.jenkins.JenkinsServerHelper;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.Callback;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.Step;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.log.StepLogBuilder;
import net.atayun.bazooka.deploy.biz.v2.util.MessageDesensitizationUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author Ping
 */
public abstract class Step4Jenkins extends Step implements Callback {

    @Autowired
    private JenkinsServerHelper jenkinsServerHelper;

    @Autowired
    protected JenkinsJobPropertiesHelper jenkinsJobPropertiesHelper;

    @Override
    public void doWork(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        StepLogBuilder stepLogBuilder = new StepLogBuilder();
        String jobName = getJobName();
        stepLogBuilder.append("Job名称:" + jobName);
        Map<String, String> jobParam = getJobParam(appOpt, appOptFlowStep);
        stepLogBuilder.append("Job输入参数:\n" + jobParam);
        try {
            this.jenkinsServerHelper.buildJob(jobName, jobParam);
        } catch (Throwable throwable) {
            stepLogBuilder.append(throwable);
            throw throwable;
        } finally {
            getStepLogCollector().collect(appOptFlowStep, stepLogBuilder.build());
        }
    }

    protected abstract Map<String, String> getJobParam(AppOpt appOpt, AppOptFlowStep appOptFlowStep);

    /**
     * 随机获取
     *
     * @return name
     */
    protected abstract String getJobName();

    @Override
    public void callback(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {
        Map<String, Object> output = appOptFlowStep.getOutput();
        int buildNumber = Integer.parseInt((String) output.get("buildNumber"));
        String jobName = (String) output.get("jobName");
        String jenkinsLog = jenkinsServerHelper.getJenkinsLog(jobName, buildNumber);
        //优化
        String log = MessageDesensitizationUtil.replaceGitCloneCmd(jenkinsLog);
        log = MessageDesensitizationUtil.replaceDockerCmd(log);
        log = MessageDesensitizationUtil.replaceUsernameE(log);
        log = MessageDesensitizationUtil.replacePasswordE(log);
        getStepLogCollector().collect(appOptFlowStep, "Job日志:\n" + log);
        getStepLogCollector().collect(appOptFlowStep, "Job输出参数:\n" + output.toString());
    }
}