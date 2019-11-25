package net.atayun.bazooka.deploy.biz.v2.dal.entity.app;

import com.youyu.common.entity.JdbcMysqlEntity;
import lombok.Getter;
import lombok.Setter;
import net.atayun.bazooka.deploy.biz.v2.enums.FlowStepStatusEnum;

import java.util.Map;

/**
 * @author Ping
 */
@Getter
@Setter
public class AppOptFlowStep extends JdbcMysqlEntity<Long> implements Cloneable {

    private Long optId;

    private Integer stepSeq;

    private String step;

    private FlowStepStatusEnum status;

    private Map<String, Object> input;

    private Map<String, Object> output;

    private String logPath;

}
