package net.atayun.bazooka.rms.biz.dal.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author Ping
 */
@Getter
@Setter
public class ResourceSumEnvGroupEntity {

    private Long id;

    private String code;

    private String name;

    private BigDecimal cpus = BigDecimal.ZERO;

    private BigDecimal memory = BigDecimal.ZERO;

    private BigDecimal disk = BigDecimal.ZERO;
}
