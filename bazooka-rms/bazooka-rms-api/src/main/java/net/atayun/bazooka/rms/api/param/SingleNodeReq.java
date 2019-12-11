package net.atayun.bazooka.rms.api.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * created by zhangyingbin on 2019/11/13 0013 下午 2:22
 * description:
 */
@Data
public class SingleNodeReq {

    @Size(max = 128,message = "节点ip数据过长")
    @ApiModelProperty("节点ip")
    private String nodeIp;

    @ApiModelProperty("ssh端口号")
    @Size(max = 5,message = "ssh端口号数据过长")
    private String sshPort;

    @ApiModelProperty("cpu")
    @Max(value = 999 , message = "cpu参数过大")
    private BigDecimal cpu;

    @ApiModelProperty("内存")
    @Max(value = 999999 , message = "内存参数过大")
    private BigDecimal memory;

    @ApiModelProperty("节点登录凭据ID")
    private Long credentialId;

    public BigDecimal getMemory() {
        return memory.multiply(new BigDecimal(1024));
    }

    public void setMemory(BigDecimal memory) {
        this.memory = memory;
    }
}
