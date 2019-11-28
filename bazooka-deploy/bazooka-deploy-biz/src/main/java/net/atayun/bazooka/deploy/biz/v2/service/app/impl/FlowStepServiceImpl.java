package net.atayun.bazooka.deploy.biz.v2.service.app.impl;

import net.atayun.bazooka.base.service.BatchService;
import net.atayun.bazooka.deploy.biz.v2.dal.dao.app.AppOptFlowStepMapper;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import net.atayun.bazooka.deploy.biz.v2.dto.app.DeployingFlowDto;
import net.atayun.bazooka.deploy.biz.v2.service.app.FlowStepService;
import net.atayun.bazooka.deploy.biz.v2.service.app.step.log.StepLogCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
@Service
public class FlowStepServiceImpl implements FlowStepService {

    @Autowired
    private AppOptFlowStepMapper appOptFlowStepMapper;

    @Autowired
    private BatchService batchService;

    @Override
    public void saveFlowSteps(List<AppOptFlowStep> flowSteps) {
        batchService.batchDispose(flowSteps, AppOptFlowStepMapper.class, "insertSelective");
    }

    @Override
    public AppOptFlowStep selectById(Long stepId) {
        return appOptFlowStepMapper.selectByPrimaryKey(stepId);
    }

    @Override
    public AppOptFlowStep selectByOptIdAndStep(Long optId, String step) {
        Example example = new Example(AppOptFlowStep.class);
        example.createCriteria().andEqualTo("optId", optId)
                .andEqualTo("step", step);
        return appOptFlowStepMapper.selectOneByExample(example);
    }

    @Override
    public AppOptFlowStep nextStep(AppOptFlowStep appOptFlowStep) {
        Example example = new Example(AppOptFlowStep.class);
        example.createCriteria().andEqualTo("optId", appOptFlowStep.getOptId())
                .andEqualTo("stepSeq", appOptFlowStep.getStepSeq() + 1);
        return appOptFlowStepMapper.selectOneByExample(example);
    }

    @Override
    public Object getFromBeforeOutput(AppOptFlowStep appOptFlowStep, String key) {
        Example example = new Example(AppOptFlowStep.class);
        example.createCriteria().andEqualTo("optId", appOptFlowStep.getOptId())
                .andLessThan("stepSeq", appOptFlowStep.getStepSeq());
        example.orderBy("stepSeq").asc();
        List<AppOptFlowStep> appOptFlowSteps = appOptFlowStepMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(appOptFlowSteps)) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        for (AppOptFlowStep optFlowStep : appOptFlowSteps) {
            map.putAll(optFlowStep.getOutput());
        }
        return map.get(key);
    }

    @Override
    public void update(AppOptFlowStep appOptFlowStep) {
        appOptFlowStepMapper.updateByPrimaryKeySelective(appOptFlowStep);
    }

    @Override
    public List<AppOptFlowStep> selectByOptId(Long optId) {
        Example example = new Example(AppOptFlowStep.class);
        example.createCriteria().andEqualTo("optId", optId);
        return appOptFlowStepMapper.selectByExample(example);
    }

    @Override
    public List<DeployingFlowDto> getDeployingFlow(Long optId) {
        List<AppOptFlowStep> appOptFlowSteps = selectByOptId(optId);
        List<AppOptFlowStep> filter = new ArrayList<>();
        for (AppOptFlowStep appOptFlowStep : appOptFlowSteps) {
            if (appOptFlowStep.isStandBy()) {
                break;
            }
            filter.add(appOptFlowStep);
            if (appOptFlowStep.isFailure()) {
                break;
            }
        }
        final StepLogCollector stepLogCollector = getBean(StepLogCollector.class);
        return filter.stream()
                .map(appOptFlowStep -> {
                    DeployingFlowDto deployingFlowDto = new DeployingFlowDto();
                    deployingFlowDto.setDeployFlowId(appOptFlowStep.getId());
                    deployingFlowDto.setDisplayName(appOptFlowStep.getStep());
                    deployingFlowDto.setDeployId(appOptFlowStep.getOptId());
                    deployingFlowDto.setStatus(appOptFlowStep.getStatus());
                    if (appOptFlowStep.isSuccess() || appOptFlowStep.isFailure()) {
                        deployingFlowDto.setFinishDatetime(appOptFlowStep.getUpdateTime());
                    }
                    deployingFlowDto.setLog(stepLogCollector.get(appOptFlowStep));
                    return deployingFlowDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void cancel(AppOptFlowStep appOptFlowStep) {
        appOptFlowStepMapper.cancelRestSteps(appOptFlowStep.getOptId(), appOptFlowStep.getStepSeq());
    }
}
