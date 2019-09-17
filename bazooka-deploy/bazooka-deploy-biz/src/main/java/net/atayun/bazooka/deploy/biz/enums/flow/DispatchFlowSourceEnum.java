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
package net.atayun.bazooka.deploy.biz.enums.flow;

/**
 * DispatchFlow 事件源
 *
 * @author Ping
 * @date 2019-07-12
 */
public enum DispatchFlowSourceEnum {

    /**
     * 发布操作.
     * 发布操作表明整个流程刚开始
     */
    DEPLOY_ACTION,

    /**
     * jenkins回调.
     * 在jenkins job执行过程中回调deploy服务
     */
    JENKINS_CALLBACK,

    /**
     * Marathon事件
     */
    MARATHON_EVENT,

    /**
     * 流程流转
     */
    FLOW
}
