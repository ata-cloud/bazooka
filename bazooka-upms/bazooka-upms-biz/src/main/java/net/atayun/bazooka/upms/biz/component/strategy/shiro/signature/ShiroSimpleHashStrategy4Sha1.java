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
package net.atayun.bazooka.upms.biz.component.strategy.shiro.signature;

import net.atayun.bazooka.combase.annotation.StrategyNum;
import net.atayun.bazooka.upms.biz.component.properties.ShiroProperties;
import org.apache.shiro.crypto.hash.Sha1Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work shiro simpleHash sha-1策略
 */
@Component
@StrategyNum(superClass = ShiroSimpleHashStrategy.class, number = "sha-1", describe = "sha-1算法")
public class ShiroSimpleHashStrategy4Sha1 extends ShiroSimpleHashStrategy {

    @Autowired
    private ShiroProperties shiroProperties;

    @Override
    public String signature(String password) {
        SimpleHash simpleHash = new Sha1Hash(password, shiroProperties.getSalt());
        return simpleHash.toHex();
    }
}
