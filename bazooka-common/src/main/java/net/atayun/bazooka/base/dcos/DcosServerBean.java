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
package net.atayun.bazooka.base.dcos;

import lombok.extern.slf4j.Slf4j;
import mesosphere.dcos.client.DCOS;
import mesosphere.dcos.client.DCOSClient;
import mesosphere.dcos.client.model.DCOSAuthCredentials;
import net.atayun.bazooka.base.dcos.api.DcosApi;
import net.atayun.bazooka.base.dcos.api.DcosApiClient;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月18日 17:00:00
 * @work Dcos服务bean组件
 */
@Slf4j
public class DcosServerBean {

    /**
     * node slave节点url地址后缀
     */
    public static final String NODE_URL_SUFFIX = ":5050/master/state-summary";
    /**
     * node节点健康url地址后缀
     */
    public static final String NODE_URL_HEALTH_SUFFIX = "/system/health/v1/nodes";
    /**
     * app信息url地址后缀
     */
    public static final String APPS_SUFFIX = ":8080/v2/apps";
    /**
     * task信息url地址后缀
     */
    public static final String TASKS_SUFFIX = ":8080/v2/tasks";
    /**
     * MESOS task信息url地址后缀
     */
    public static final String MESOS_TASKS_SUFFIX = ":5050/master/tasks?limit=2000";

    /**
     * 获取DCOS实例
     *
     * @param endpoint
     * @return
     */
    public DcosApi getInstance(String endpoint) {
        return DcosApiClient.getInstance(endpoint);
    }



    /**
     * 获取DCOS实例
     *
     * @param endpoint
     * @param authCredentials
     * @return
     */
    public static DCOS getInstance(String endpoint, DCOSAuthCredentials authCredentials) {
        return DCOSClient.getInstance(endpoint, authCredentials);
    }
}
