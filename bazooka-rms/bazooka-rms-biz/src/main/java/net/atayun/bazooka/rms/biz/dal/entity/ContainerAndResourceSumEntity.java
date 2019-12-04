package net.atayun.bazooka.rms.biz.dal.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author Ping
 */
@Setter
@Getter
public class ContainerAndResourceSumEntity {

    private Integer count = 0;

    private BigDecimal cpu = BigDecimal.ZERO;

    private BigDecimal memory = BigDecimal.ZERO;

    private BigDecimal disk = BigDecimal.ZERO;
}
