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

import net.atayun.bazooka.base.config.DcosProperties;
import net.atayun.bazooka.base.constant.CommonConstants;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author Ping
 * @date 2019-08-12
 */
public class DcosPropertiesHelper {

    @Autowired
    private DcosProperties dcosProperties;

    public String dcosMasterIp() {
        @NotEmpty List<String> masterIp = dcosProperties.getMasterIp();
        return CommonConstants.PROTOCOL + masterIp.get(RandomUtils.nextInt(0, masterIp.size()));
    }

    public String dcosPublicAgentIp() {
        @NotEmpty List<String> publicAgentIp = dcosProperties.getPublicAgentIp();
        return CommonConstants.PROTOCOL + publicAgentIp.get(RandomUtils.nextInt(0, publicAgentIp.size()));
    }
}
