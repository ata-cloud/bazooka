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
    @NotNull(message = "集群名称不能为空")
    private String name;

    @ApiModelProperty("集群类型")
    @NotNull(message = "集群类型不能为空")
    private String type;

    @ApiModelProperty("镜像库url")
    @NotNull(message = "镜像库url不能为空")
    private String imageUrl;

    @ApiModelProperty("镜像库凭据ID")
    private Long credentialId;

    @ApiModelProperty("机房类型")
    private String roomType;

    @ApiModelProperty("集群版本号")
    private String version;

    @ApiModelProperty("Master节点ip列表集合")
    private List<String> masterUrls;

    @ApiModelProperty("集群mlb ip列表集合")
    private List<String> mlbUrls;

    @ApiModelProperty("节点集合")
    private List<SingleNodeReq> nodeList;
}

