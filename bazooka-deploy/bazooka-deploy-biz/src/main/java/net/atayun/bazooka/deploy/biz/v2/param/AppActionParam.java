package net.atayun.bazooka.deploy.biz.v2.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import net.atayun.bazooka.deploy.biz.v2.enums.AppOptEnum;

import javax.validation.constraints.NotNull;

/**
 * @author Ping
 */
@Getter
@Setter
@ApiModel(description = "服务操作")
public class AppActionParam {

    @NotNull
    @ApiModelProperty("操作事件类型")
    private AppOptEnum event;

    @NotNull
    @ApiModelProperty("服务Id")
    private Long appId;

    @NotNull
    @ApiModelProperty("环境id")
    private Long envId;

    @NotNull
    @ApiModelProperty("操作事件的参数(Json格式)")
    private String detail;

}
