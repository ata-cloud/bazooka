package net.atayun.bazooka.delpoy2.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import net.atayun.bazooka.combase.enums.deploy.AppOperationEnum;

import javax.validation.constraints.NotNull;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 上午10:14
 */
@Data
public class DeployCommandReqDto {


    @NotNull
    @ApiModelProperty("操作事件类型")
    private AppOperationEnum appOperationEnum;

    @NotNull
    @ApiModelProperty("服务Id")
    private Long appId;

    @NotNull
    @ApiModelProperty("环境id")
    private Long envId;

    @NotNull
    @ApiModelProperty("操作事件的参数(Json格式)")
    private String detail;


    private Long deployConfigId;

}
