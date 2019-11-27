package net.atayun.bazooka.deploy.biz.v2.service.app.flow;

import com.youyu.common.exception.BizException;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppOptService;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppStatusOpt;
import net.atayun.bazooka.deploy.biz.v2.service.app.FlowStepService;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.StepWorker;
import net.atayun.bazooka.deploy.biz.v2.service.app.threadpool.FlowStepThreadPool;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
@Component
public class FlowDispatcher {

    @Async
    @EventListener
    public void dispatcher(FlowDispatcherEvent event) {
        StepWorker stepWorker = event.getStepWorker();
        AppOpt appOpt = stepWorker.getAppOpt();
        AppOptFlowStep appOptFlowStep = stepWorker.getAppOptFlowStep();

        if (appOptFlowStep.isStandBy()) {
            FlowStepThreadPool.execute(stepWorker::doWork);
            return;
        }

        FlowStepService flowStepService = getBean(FlowStepService.class);
        //执行过程中可能被取消
        AppOptFlowStep mayCancel = flowStepService.selectById(appOptFlowStep.getId());
        if (mayCancel.isCancel()) {
            appOptFlowStep.cancel();
        }
        //更新
        flowStepService.update(appOptFlowStep);

        try {
            if (appOptFlowStep.isCancel() || appOptFlowStep.isFailure()) {
                appOpt.failure();
                return;
            }
            if (appOptFlowStep.isSuccess()) {
                AppOptFlowStep nextStep = flowStepService.nextStep(appOptFlowStep);
                if (nextStep == null) {
                    appOpt.success();
                    return;
                }
                //还未开始执行就已经被取消
                if (nextStep.isCancel()) {
                    appOpt.failure();
                    return;
                }
                if (nextStep.isStandBy()) {
                    nextStep.setInput(appOptFlowStep.getOutput());
                    FlowStepThreadPool.execute(() -> new StepWorker(appOpt, nextStep).doWork());
                }
            }
        } catch (Throwable throwable) {
            appOpt.failure();
            throw new BizException("", "操作异常", throwable);
        } finally {
            if (appOpt.isFinish()) {
                getBean(AppOptService.class).update(appOpt);
                AppStatusOpt.updateAppStatus(appOpt, Boolean.FALSE);
            }
        }
    }
}
