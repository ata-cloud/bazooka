package net.atayun.bazooka.rms.api.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * created by zhangyingbin on 2019/11/8 0008 上午 11:07
 * description:
 */
@Data
public class CreateClusterReq {

    @ApiModelProperty("集群名称")
    @NotNull
    private String name;

    @ApiModelProperty("集群类型")
    @NotNull
    private String type;

    @ApiModelProperty("镜像库,type:2")
    @NotNull
    private UrlType imageUrlType;

    @ApiModelProperty("镜像库凭据ID")
    private Long credentialId;

    @ApiModelProperty("机房类型")
    private String roomType;

    @ApiModelProperty("集群版本号")
    private String version;

    @ApiModelProperty("0、Master节点ip列表;1、集群mlb")
    private List<UrlType> urls;

    @ApiModelProperty("节点集合")
    private List<SingleNode> nodeList;


    @Data
    public class UrlType {

        @ApiModelProperty("类型")
        private String type;

        @ApiModelProperty("url")
        private String url;
    }

    @Data
    public class SingleNode {

        @ApiModelProperty("节点ip")
        private String nodeIp;

        @ApiModelProperty("cpu")
        private BigDecimal cpu;

        @ApiModelProperty("内存")
        private BigDecimal memory;

        @ApiModelProperty("节点登录凭据ID")
        private Long credentialId;
    }
}

