package net.atayun.bazooka.deploy.biz.v2.dal.dao.app;

import com.youyu.common.mapper.YyMapper;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptFlowStep;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author Ping
 */
public interface AppOptFlowStepMapper extends YyMapper<AppOptFlowStep> {

    @Update("update deploy_app_opt_flow_step set status = 'CANCEL' where apt_id = #{optId} and step_seq >= #{stepSeq};")
    void cancelRestSteps(@Param("optId") Long optId, @Param("stepSeq") Integer stepSeq);
}
