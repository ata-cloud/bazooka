package net.atayun.bazooka.rms.biz.enums;

import com.youyu.common.api.IBaseResultCode;
import lombok.Getter;

/**
 * created by zhangyingbin on 2019/11/11 0011 上午 10:48
 * description: 集群类型枚举
 */
@Getter
public enum ClusterTypeEnum implements IBaseResultCode {

    MESOS("0","mesos集群"),
    KUBERNETES("1","Kubernetes集群"),
    SINGLENODE("2","单节点集群");

    private String code;

    private String desc;

    ClusterTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
