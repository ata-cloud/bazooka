package net.atayun.bazooka.deploy.biz.v2.dal.dao.app;

import com.youyu.common.mapper.YyMapper;
import net.atayun.bazooka.base.enums.AppOptEnum;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOpt;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.AppOptHis;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.DeployCountsEntity;
import net.atayun.bazooka.deploy.biz.v2.dal.entity.app.EventWithMarathonEntity;
import net.atayun.bazooka.deploy.biz.v2.enums.AppOptStatusEnum;
import net.atayun.bazooka.deploy.biz.v2.param.AppOptHisMarathonParam;
import net.atayun.bazooka.deploy.biz.v2.param.AppOptHisParam;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Ping
 */
public interface AppOptMapper extends YyMapper<AppOpt> {

    @Select("select " +
            "d.id event_id, " +
            "d.create_time operate_datetime, " +
            "d.create_author operator, " +
            "d.opt event, " +
            "d.detail, " +
            "d.status, " +
            "d.remark " +
            "from deploy_app_opt d " +
            "where d.app_id = #{pageData.appId} and d.env_id = #{pageData.envId} " +
            "order by d.create_time desc ")
    List<AppOptHis> getAppOptHis(@Param("pageData") AppOptHisParam pageParam);

    @Select("select " +
            "d.id event_id, " +
            "d.app_id, " +
            "d.env_id, " +
            "d.opt event, " +
            "d.detail, " +
            "d.status, " +
            "d.app_deploy_config marathon_config, " +
            "d.app_deploy_version marathon_deployment_version " +
            "from deploy_app_opt d " +
            "where d.app_id = #{pageData.appId} and d.env_id = #{pageData.envId} and d.opt in ('MARATHON_BUILD_DEPLOY', 'NODE_BUILD_DEPLOY', 'SCALE', 'ROLLBACK')" +
            "order by d.id desc ")
    List<EventWithMarathonEntity> getAppOptHisMarathon(@Param("pageData") AppOptHisMarathonParam pageParam);

    @Select("select " +
            "* " +
            "from deploy_app_opt d " +
            "where d.app_id = #{appId} and d.env_id = #{envId} and d.status = #{status} and d.event = #{event} ")
    AppOpt selectRollbackEntity(@Param("appId") Long appId, @Param("envId") Long envId, @Param("status") AppOptStatusEnum status, @Param("event") AppOptEnum event);

    @Select("select " +
            "app_id, " +
            "app_name, " +
            "count(0) counts " +
            "from deploy_app_opt " +
            "where project_id = #{projectId} and create_time >= #{leftDatetime} " +
            "group by app_id, app_name " +
            "order by counts desc;")
    List<DeployCountsEntity> deployCountsByProject(@Param("projectId") Long projectId, @Param("leftDatetime") LocalDateTime leftDatetime);
}
