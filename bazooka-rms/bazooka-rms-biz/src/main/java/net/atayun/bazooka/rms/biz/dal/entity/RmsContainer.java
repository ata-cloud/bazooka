package net.atayun.bazooka.rms.biz.dal.entity;

import com.youyu.common.entity.JdbcMysqlEntity;
import lombok.Getter;
import lombok.Setter;
import net.atayun.bazooka.rms.api.enums.ClusterAppServiceStatusEnum;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Ping
 */
@Getter
@Setter
@Table(name = "rms_container")
public class RmsContainer extends JdbcMysqlEntity<Long> {

    private Long clusterId;

    private Long envId;

    private Long nodeId;

    private Long appId;

    private String ip;

    private String containerName;

    @Column(name = "container_status")
    private ClusterAppServiceStatusEnum containerStatus;

    private String containerImage;

    private BigDecimal cpu;

    private BigDecimal memory;

    private BigDecimal disk;

    @Column(name = "port_mapping")
    private List<String> portMapping;

    @Column(name = "env_variable")
    private List<String> envVariable;

    @Column(name = "volume")
    private List<String> volume;
}
