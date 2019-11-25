package net.atayun.bazooka.deploy.biz.v2.service.app.step.startapp;

import net.atayun.bazooka.base.annotation.StrategyNum;
import net.atayun.bazooka.deploy.biz.v2.constant.FlowStepConstants;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppOptService;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.SinglePhaseStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.Step;
import org.springframework.stereotype.Component;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;


/**
 * @author Ping
 */
@Component
@StrategyNum(superClass = Step.class, number = FlowStepConstants.START_APP)
public class Step4StartApp extends SinglePhaseStep {


    @Override
    public void doWork(AppOpt appOpt, AppOptFlowStep appOptFlowStep) {

//        stepTool.doWork(appOpt, appOptFlowStep);
        AppOptService appOptService = getBean(AppOptService.class);
        appOptService.updateById(appOpt);

    }
}
