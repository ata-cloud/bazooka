package net.atayun.bazooka.gateway.component.strategy;

import com.youyu.common.api.Result;
import net.atayun.bazooka.base.dcos.dto.MarathonEventDeployDto;
import net.atayun.bazooka.deploy.api.param.MarathonCallbackParam;
import net.atayun.bazooka.deploy.biz.v2.controller.MarathonCallbackController;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.alibaba.fastjson.JSON.parseObject;
import static net.atayun.bazooka.base.bean.SpringContextBean.getBean;
import static net.atayun.bazooka.base.enums.status.FinishStatusEnum.FAILURE;
import static net.atayun.bazooka.base.enums.status.FinishStatusEnum.SUCCESS;
import static net.atayun.bazooka.base.utils.StringUtil.eq;

/**
 * @author Ping
 */
@Component
public class MarathonFinishCallbackHandler implements MarathonCallbackHandler {

    @Override
    public boolean support(String event) {
        return Objects.equals(event, ClusterDeployStrategy4Dcos.EVENT_TYPE_SUCCESS) || Objects.equals(event, ClusterDeployStrategy4Dcos.EVENT_TYPE_FAILED);
    }

    @Override
    public Result handle(Long clusterId, String data) {
        MarathonEventDeployDto marathonEventDeploy = parseObject(data, MarathonEventDeployDto.class);
        return getBean(MarathonCallbackController.class).marathonCallback(getMarathonCallbackParam(marathonEventDeploy));
    }


    /**
     * 获取MarathonCallbackParam对象
     *
     * @param marathonEventDeploy marathonEventDeploy
     * @return MarathonCallbackParam
     */
    private MarathonCallbackParam getMarathonCallbackParam(MarathonEventDeployDto marathonEventDeploy) {
        MarathonCallbackParam marathonCallbackParam = new MarathonCallbackParam();

        marathonCallbackParam.setMarathonDeploymentId(marathonEventDeploy.getId());
        marathonCallbackParam.setMarathonDeploymentVersion(marathonEventDeploy.getPlan().getVersion());
        marathonCallbackParam.setFinishStatus(eq(marathonEventDeploy.getEventType(), ClusterDeployStrategy4Dcos.EVENT_TYPE_SUCCESS) ? SUCCESS : FAILURE);
        return marathonCallbackParam;
    }
}
