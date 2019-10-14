package net.atayun.bazooka.combase.dcos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月18日 17:00:00
 * @work dcos slave Attributes信息
 */
@Setter
@Getter
public class NodeSlaveAttributesDto implements Serializable {

    private static final long serialVersionUID = -6584506542411040616L;

    @ApiModelProperty("是否是master")
    @JsonProperty(value = "public_ip")
    private Boolean publicIp;
}
