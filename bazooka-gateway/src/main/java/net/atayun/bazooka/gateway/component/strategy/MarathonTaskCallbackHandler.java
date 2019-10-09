package net.atayun.bazooka.gateway.component.strategy;

import com.alibaba.fastjson.JSONObject;
import com.youyu.common.api.Result;
import net.atayun.bazooka.base.dcos.dto.MarathonEventTaskDto;
import net.atayun.bazooka.deploy.api.AppOperationEventApi;
import net.atayun.bazooka.deploy.api.param.MarathonTaskFailureCallbackParam;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;

/**
 * @author Ping
 */
@Component
public class MarathonTaskCallbackHandler implements MarathonCallbackHandler {

    private static final String FAIL = "TASK_FAILED;";

    private static final Long TIMEOUT = 10L;

    @Override
    public boolean support(String event) {
        return Objects.equals(event, ClusterDeployStrategy4Dcos.EVENT_TYPE_STATUS_UPDATE_EVENT);
    }

    @Override
    public Result handle(Long clusterId, String data) {
        MarathonEventTaskDto marathonEventTaskDto = JSONObject.parseObject(data, MarathonEventTaskDto.class);
        String taskStatus = marathonEventTaskDto.getTaskStatus();
        if (!FAIL.contains(taskStatus)) {
            return Result.ok();
        }
        //version和timestamp的格式:2019-01-01T00:00:00.000Z
        Instant start = Instant.parse(marathonEventTaskDto.getVersion());
        Instant now = Instant.parse(marathonEventTaskDto.getTimestamp());
        if (start.plus(TIMEOUT, ChronoUnit.MINUTES).isAfter(now)) {
            return Result.ok();
        }
        return getBean(AppOperationEventApi.class).marathonTaskFailureCallback(getMarathonTaskFailureCallbackParam(clusterId, marathonEventTaskDto));
    }

    private MarathonTaskFailureCallbackParam getMarathonTaskFailureCallbackParam(Long clusterId, MarathonEventTaskDto marathonEventTaskDto) {
        MarathonTaskFailureCallbackParam marathonTaskFailureCallbackParam = new MarathonTaskFailureCallbackParam();
        marathonTaskFailureCallbackParam.setClusterId(clusterId);
        marathonTaskFailureCallbackParam.setMarathonDeploymentVersion(marathonEventTaskDto.getVersion());
        marathonTaskFailureCallbackParam.setMarathonServiceId(marathonEventTaskDto.getAppId());
        marathonTaskFailureCallbackParam.setMessage(marathonEventTaskDto.getMessage());
        return marathonTaskFailureCallbackParam;
    }
}
