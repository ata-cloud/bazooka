package net.atayun.bazooka.rms.biz.dal.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author Ping
 */
@Getter
@Setter
public class ResourceSumEntity {

    private BigDecimal cpu = BigDecimal.ZERO;

    private BigDecimal memory = BigDecimal.ZERO;

    private BigDecimal disk = BigDecimal.ZERO;

}
