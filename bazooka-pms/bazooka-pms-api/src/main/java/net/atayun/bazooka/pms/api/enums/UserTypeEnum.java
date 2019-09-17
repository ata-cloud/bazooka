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


import java.util.Comparator;

/**
 * @author rache
 * @date 2019-07-18
 */

public enum  UserTypeEnum implements Comparator<UserTypeEnum> {
    USER_PROJECT_DEV(20),
    USER_PROJECT_MASTER(30),
    USER_APP_MASTER(10);
    private int orderId;
    UserTypeEnum(int orderId){
        this.orderId=orderId;
    }
    public int getOrderId(){
        return this.orderId;
    }

    @Override
    public int compare(UserTypeEnum o1, UserTypeEnum o2) {
        return   o1.getOrderId()-o2.getOrderId();
    }}
