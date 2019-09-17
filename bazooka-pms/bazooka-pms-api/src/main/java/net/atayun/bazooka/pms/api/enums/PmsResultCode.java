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
package net.atayun.bazooka.pms.api.enums;

import com.youyu.common.api.IBaseResultCode;
import org.springframework.util.ObjectUtils;

import static net.atayun.bazooka.base.constant.AtaResultCode.PMS_CODE;
import static com.youyu.common.constant.ResultCodeConstant.COMMON_CODE_PREFIX;
import static com.youyu.common.constant.ResultCodeConstant.SUCCESS;

/**
 * @author superwen
 * @date 2018/10/16 下午3:40
 */
public enum PmsResultCode implements IBaseResultCode {

    NOT_APP_INSTANCE(COMMON_CODE_PREFIX + "080041", "不存在的服务 id"),
    NOT_DEPLOYED_TO_DCOS(SUCCESS, "暂未部署到DC/OS"),
    MARATHON_ERROR(COMMON_CODE_PREFIX + "080011", "调用 marathon接口失败"),
    CANCEL_MARATHON_FAILED(COMMON_CODE_PREFIX + "080012", "取消 Marathon 部署失败"),
    JENKINS_CREATE_ERROR(COMMON_CODE_PREFIX + "080020", "jenkins创建失败"),
    JENKINS_UPDATE_ERROR(COMMON_CODE_PREFIX + "080021", "jenkins修改失败"),
    CONFIG_NO_EXISTS(COMMON_CODE_PREFIX + "080022", "配置不存在"),
    JENKINS_CONFIG_NO_EXISTS(COMMON_CODE_PREFIX + "080023", "jenkins job不存在"),
    JENKINS_OPERATION_ERROR(COMMON_CODE_PREFIX + "080024", "jenkins 操作异常"),
    DOCKER_TAG_NO_EXISTS(COMMON_CODE_PREFIX + "080025", "docker tag不存在"),

    PROJECT_META_INFO_NOT_FOUND(COMMON_CODE_PREFIX + "080101", "项目配置信息不存在"),
    CLUSTER_NO_EXISTS(COMMON_CODE_PREFIX + "080102", "集群信息不存在"),
    CLUSTER_TRANSFER_SRC_EQ_DEST(COMMON_CODE_PREFIX + "080103", "源集群与目标集群相同"),
    GITLAB_CREATE_ERROR(PMS_CODE+"1001","gitlab创建异常"),
    GITLAB_DELETE_ERROR(PMS_CODE+"1002","gitlab删除异常"),
    PROJECT_SAME_INFO(PMS_CODE+"1003","项目名称和项目code已经存在"),
    PORT_EXIST(PMS_CODE+"1004","环境端口已经存在"),
    PROJECT_MASTER_ERROR(PMS_CODE+"1005","项目负责人必填"),
    PMS_TOP_ERROR(PMS_CODE+"1006","置顶异常"),
    GITLAB_MEMBER_ADD_ERROR(PMS_CODE+"1007","gitlab添加用户异常"),
    PMS_NO_GITLAB_USER(PMS_CODE+"1008","未找到gitlab用户相关信息"),
    ENV_NOT_CHANGE(PMS_CODE+"1009","已经分配该环境"),
    PROJRDCT_DEV_ERROR(PMS_CODE+"1010","项目参与人添加失败"),
    PROJECT_MASTER_INTO_ERROR(PMS_CODE+"1011","项目负责人添加失败"),
    NOT_PROJECT(COMMON_CODE_PREFIX + "1012", "不存在的项目 id"),
    PROJECT_SKIP_ERROR(PMS_CODE+"1013","项目添加失败（可跳过）"),
    ;

    private String code;
    private String desc;

    PmsResultCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public static IBaseResultCode byCode(String code) {
        if (ObjectUtils.isEmpty(code)) {
            return null;
        } else {
            PmsResultCode[] var1 = values();
            int var2 = var1.length;

            for (int var3 = 0; var3 < var2; ++var3) {
                IBaseResultCode e = var1[var3];
                if (e.getCode().equals(code)) {
                    return e;
                }
            }

            return null;
        }
    }
}
