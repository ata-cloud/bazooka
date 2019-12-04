package net.atayun.bazooka.rms.api.param;

import lombok.Getter;
import lombok.Setter;
import net.atayun.bazooka.rms.api.enums.ClusterAppServiceStatusEnum;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Ping
 */
@Getter
@Setter
public class NodeContainerParam {

    private Long clusterId;

    private Long envId;

    private Long nodeId;

    private Long appId;

    private String ip;

    private String containerName;

    private ClusterAppServiceStatusEnum containerStatus;

    private String containerImage;

    private BigDecimal cpu;

    private BigDecimal memory;

    private BigDecimal disk;

    private List<String> portMapping;

    private List<String> envVariable;

    private List<String> volume;
}
