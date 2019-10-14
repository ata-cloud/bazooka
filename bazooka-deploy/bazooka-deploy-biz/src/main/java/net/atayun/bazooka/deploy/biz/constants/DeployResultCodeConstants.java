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
package net.atayun.bazooka.deploy.biz.constants;

import net.atayun.bazooka.combase.constant.AtaResultCode;

/**
 * @author Ping
 * @date 2019-08-16
 */
public final class DeployResultCodeConstants {

    private DeployResultCodeConstants() {
        throw new AssertionError("Must not instantiate constant utility class");
    }

    public static final String LOG_PATH_ERR_CODE = AtaResultCode.DEPLOY_CODE + "0001";

    public static final String APP_IS_DEPLOYING_ERR_CODE = AtaResultCode.DEPLOY_CODE + "0002";

    public static final String APP_DEPLOY_MARATHON_CALLBACK_ERR_CODE = AtaResultCode.DEPLOY_CODE + "0003";

    public static final String MISS_DEPLOY_ENTITY_ERR_CODE = AtaResultCode.DEPLOY_CODE + "0004";

    public static final String MULTI_DEPLOYING_ENTITY_ERR_CODE = AtaResultCode.DEPLOY_CODE + "0005";

    public static final String ILLEGAL_DEPLOY_ID_ERR_CODE = AtaResultCode.DEPLOY_CODE + "0006";

    public static final String ILLEGAL_DEPLOY_FLOW_ID_ERR_CODE = AtaResultCode.DEPLOY_CODE + "0007";

    public static final String EMPTY_DEPLOY_ERR_CODE = AtaResultCode.DEPLOY_CODE + "0008";

    public static final String APP_INSTANCE_ZERO = AtaResultCode.DEPLOY_CODE + "0009";

    public static final String ILLEGAL_CPU_SHARES = AtaResultCode.DEPLOY_CODE + "0010";

    public static final String ILLEGAL_MEM = AtaResultCode.DEPLOY_CODE + "0011";

    public static final String ILLEGAL_DISK = AtaResultCode.DEPLOY_CODE + "0012";

    public static final String ILLEGAL_BRANCH = AtaResultCode.DEPLOY_CODE + "0013";

    public static final String BRANCH_REQ_ERR = AtaResultCode.DEPLOY_CODE + "0014";

    public static final String IMAGE_EMPTY = AtaResultCode.DEPLOY_CODE + "0015";

    public static final String ILLEGAL_IMAGE_TAG = AtaResultCode.DEPLOY_CODE + "0016";

    public static final String UNKNOWN_FLOW = AtaResultCode.DEPLOY_CODE + "0017";

    public static final String BUILD_SCRIPT_CALLBACK_ERR_CODE = AtaResultCode.DEPLOY_CODE + "0018";

    public static final String NO_SUCCESS_EVENT_WITH_MARATHON_ERR_CODE = AtaResultCode.DEPLOY_CODE + "0019";

    public static final String GIT_URI_ERROR = AtaResultCode.DEPLOY_CODE + "0020";

    public static final String ILLEGAL_EVENT_ID_ERR_CODE = AtaResultCode.DEPLOY_CODE + "0021";
}
