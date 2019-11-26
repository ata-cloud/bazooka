package net.atayun.bazooka.deploy.biz.v2.service.app.opt;

import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.enums.FlowStepStatusEnum;

import java.util.Map;

/**
 * @author Ping
 */
public class AppOptFlowStepBuilder {

    private Long optId;

    private Integer seq;

    private String step;

    private Map<String, Object> input;

    public AppOptFlowStepBuilder() {
    }

    public AppOptFlowStepBuilder setOptId(Long optId) {
        this.optId = optId;
        return this;
    }

    public AppOptFlowStepBuilder setSeq(Integer seq) {
        this.seq = seq;
        return this;
    }

    public AppOptFlowStepBuilder setStep(String step) {
        this.step = step;
        return this;
    }

    public AppOptFlowStepBuilder setInput(Map<String, Object> input) {
        this.input = input;
        return this;
    }

    public AppOptFlowStep build() {

        //空值校验

        AppOptFlowStep appOptFlowStep = new AppOptFlowStep();
        appOptFlowStep.setOptId(optId);
        appOptFlowStep.setStatus(FlowStepStatusEnum.STAND_BY);
        appOptFlowStep.setStep(step);
        appOptFlowStep.setStepSeq(seq);
        appOptFlowStep.setInput(input);
        String logPath = "/data/ata/log/" + optId + "/" + step;
        appOptFlowStep.setLogPath(logPath);
        return appOptFlowStep;
    }
}
