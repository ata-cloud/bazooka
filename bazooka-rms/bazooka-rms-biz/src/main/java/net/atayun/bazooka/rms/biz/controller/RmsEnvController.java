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
package net.atayun.bazooka.rms.biz.controller;

import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.biz.service.EnvService;
import com.youyu.common.api.Result;
import net.atayun.bazooka.rms.api.dto.*;
import net.atayun.bazooka.rms.api.param.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.youyu.common.api.Result.ok;

/**
 * @Author: hanxiaorui
 * @Date: 2019/7/18 15:31
 * @Description:
 */
@RestController
@RequestMapping("/envs")
public class RmsEnvController implements EnvApi {

    @Autowired
    private EnvService envService;

    @Override
    @GetMapping("/{envId:\\d+}/configuration")
    public Result<ClusterConfigDto> getEnvConfiguration(@PathVariable("envId") Long envId) {
        return ok(envService.getClusterConfiguration(envId));
    }

    @Override
    @PostMapping("/getEnvMlbPortUsedInfo")
    public Result<EnvMlbPortUsedRsp> getEnvMlbPortUsedInfo(@RequestBody @Valid EnvMlbPortUsedReq envMlbPortUsedReq) {
        return ok(envService.getEnvMlbPortUsedInfo(envMlbPortUsedReq));
    }

    @Override
    @GetMapping("/{envId:\\d+}/resource")
    public Result<EnvResourceDto> getEnvAvailableResource(@PathVariable("envId") Long envId) {
        return ok(envService.getEnvAvailableResource(envId));
    }

    @Override
    @PostMapping("/getAppServiceInfo")
    public Result<ClusterAppServiceInfoDto> getClusterAppServiceInfo(@RequestBody @Valid EnvAppReq envAppReq) {
        return ok(envService.getClusterAppServiceInfo(envAppReq));
    }

    @Override
    @PostMapping("/getAppServiceHosts")
    public Result<List<ClusterAppServiceHostDto>> getClusterAppServiceHosts(@RequestBody @Valid EnvAppReq envAppReq) {
        return ok(envService.getClusterAppServiceHosts(envAppReq));
    }

    @Override
    @PostMapping("/getClusterAppImage")
    public Result<String> getClusterAppImage(@RequestBody @Valid EnvAppReq envAppReq) {
        return ok(envService.getClusterAppImage(envAppReq));
    }

    @Override
    @PostMapping("/getAppDockers")
    public Result<List<ClusterDockerDto>> getClusterDockers(@RequestBody @Valid EnvAppReq envAppReq) {
        return ok(envService.getClusterDockers(envAppReq));
    }

    @Override
    @PostMapping("/list")
    public Result<List<EnvDto>> list(@RequestBody @Valid EnvQueryReq envQueryReq) {
        return ok(envService.list(envQueryReq));
    }

    @Override
    @PostMapping("/create")
    public Result create(@RequestBody @Valid EnvCreateReq envCreateReq) {
        envService.create(envCreateReq);
        return ok();
    }

    @Override
    @PostMapping("/update")
    public Result update(@RequestBody @Valid EnvModifyReq envModifyReq) {
        envService.update(envModifyReq);
        return ok();
    }

    @Override
    @PostMapping("/{envId:\\d+}/get")
    public Result<EnvDto> get(@PathVariable("envId") Long envId) {
        return ok(envService.get(envId));
    }

    @Override
    @PostMapping("/{envId:\\d+}/delete")
    public Result delete(@PathVariable("envId") Long envId) {
        envService.delete(envId);
        return ok();
    }
}
