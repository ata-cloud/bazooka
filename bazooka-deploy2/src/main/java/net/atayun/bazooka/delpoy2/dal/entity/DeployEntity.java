package net.atayun.bazooka.delpoy2.dal.entity;

import com.youyu.common.entity.BaseEntity;
import lombok.Data;
import net.atayun.bazooka.combase.enums.deploy.AppOperationEnum;
import net.atayun.bazooka.deploy.biz.enums.status.BasicStatusEnum;

import javax.persistence.Column;

/**
 * @Author: xiongchengwei
 * @Date: 2019/9/25 上午10:15
 */
@Data
public class DeployEntity extends BaseEntity<Long> {

    protected Long id;

    private Long appId;

    private Long deployConfigId;
    /**
     * 启动， 停止， 构建发布
     */
    private AppOperationEnum appOperationEnum;

    //Json
    private String detail;

    @Column(name = "status")
    private BasicStatusEnum status;

    private String remark;

    private String logDir;

    private String publish_version;

    private String publish_config;

    private String cluster_type;

    private String branch;
}