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
package net.atayun.bazooka.pms.api.feign;

import net.atayun.bazooka.combase.enums.deploy.AppOperationEnum;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.dto.AppInfoDto;
import net.atayun.bazooka.pms.api.dto.AppInfoWithCredential;
import net.atayun.bazooka.pms.api.dto.PmsAppDeployStatusDto;
import com.youyu.common.api.Result;
import org.springframework.web.bind.annotation.*;

/**
 * 发布配置
 *
 * @author WangSongJun
 * @date 2019-07-16
 */
@RequestMapping("app")
public interface AppApi {

    /**
     * 根据配置ID获取发布配置
     *
     * @param configId
     * @return
     */
    @GetMapping("/deploy-config/get/{configId:\\d+}")
    Result<AppDeployConfigDto> getAppDeployConfigInfoById(@PathVariable Long configId);

    /**
     * 获取服务详情(内部接口不需要验证)
     *
     * @param appId
     * @return
     */
    @GetMapping("/_inner/get/{appId:\\d+}")
    Result<AppInfoDto> getAppInfoById(@PathVariable Long appId);

    /**
     * 获取服务详情包括git库的凭证(内部接口不需要验证)
     *
     * @param appId
     * @return
     */
    @GetMapping("/_inner/get-include-credential/{appId:\\d+}")
    Result<AppInfoWithCredential> getAppInfoWithCredentialById(@PathVariable Long appId);

    /**
     * 获取服务部署状态(内部接口不需要验证)
     *
     * @param appId
     * @param envId
     * @return
     */
    @GetMapping("/_inner/deploy-status/get/{appId:\\d+}/{envId:\\d+}")
    Result<PmsAppDeployStatusDto> getAppDeployStatus(@PathVariable Long appId, @PathVariable Long envId);

    /**
     * 更新服务部署状态
     *
     * @param appId
     * @param envId
     * @param isDeploying
     * @param appOperationEnum
     * @return
     */
    @PostMapping("/deploy-status/update{appId:\\d+}/{envId:\\d+}")
    Result<PmsAppDeployStatusDto> updateAppDeployStatus(@PathVariable Long appId, @PathVariable Long envId, @RequestParam boolean isDeploying, @RequestParam(required = false) AppOperationEnum appOperationEnum);
}
