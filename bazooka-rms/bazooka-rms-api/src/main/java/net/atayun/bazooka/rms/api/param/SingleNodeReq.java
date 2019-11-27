package net.atayun.bazooka.rms.api.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * created by zhangyingbin on 2019/11/13 0013 下午 2:22
 * description:
 */
@Data
public class SingleNodeReq {

    @ApiModelProperty("节点ip")
    private String nodeIp;

    @ApiModelProperty("ssh端口号")
    private String sshPort;

    @ApiModelProperty("cpu")
    private BigDecimal cpu;

    @ApiModelProperty("内存")
    private BigDecimal memory;

    @ApiModelProperty("节点登录凭据ID")
    private Long credentialId;
}
