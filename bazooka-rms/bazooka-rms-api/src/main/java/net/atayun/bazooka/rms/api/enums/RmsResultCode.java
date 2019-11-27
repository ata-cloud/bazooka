/*
 *    Copyright 2018-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.atayun.bazooka.rms.api.enums;

import com.youyu.common.api.IBaseResultCode;

/**
 * @Author: hanxiaorui
 * @Date: 2019/7/16 18:14
 * @Description: 异常码
 */
public enum RmsResultCode implements IBaseResultCode {


    ENV_CODE_EXISTS("0001", "环境代码已存在"),
    ENV_CODE_ILLEGAL("0002", "环境代码不合法(必须为字母小写、数字、中横线且长度不超过20位)"),
    ENV_NAME_EXISTS("0003", "环境名称已存在"),
    ENV_NAME_ILLEGAL("0004", "环境名称不合法(必须为中文、字母大小写、数字、中横线且长度不超过20位)"),
    CLUSTER_RESOURCE_UNAVAILABLE("0005", "集群资源不可用"),
    CLUSTER_CPU_UNAVAILABLE("0006", "集群CPU资源不可用"),
    CLUSTER_MEMORY_UNAVAILABLE("0007", "集群内存资源不可用"),
    CLUSTER_DISK_UNAVAILABLE("0008", "集群磁盘资源不可用"),
    ENV_CPU_LESS_THAN_USED("0009", "分配CPU不能低于本环境已使用资源数"),
    ENV_MEMORY_LESS_THAN_USED("0010", "分配内存资源不能低于本环境已使用资源数"),
    ENV_DISK_LESS_THAN_USED("0011", "分配磁盘资源不能低于本环境已使用资源数"),
    ENV_NOT_EXISTS("0012", "环境信息不存在"),
    GET_INSTANCE_LOG_EXCEPTION("0013", "获取实例日志信息异常"),
    CLUSTER_NAME_HAVE_IN_DB("0014", "集群名称已经存在"),
    MASTER_NODE_IP("0015", "master节点Ip地址错误，无法获取集群版本信息"),
    ;

    private static final String RMS_PREFIX = "0106";

    private String code;
    private String desc;

    RmsResultCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return RMS_PREFIX + code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
