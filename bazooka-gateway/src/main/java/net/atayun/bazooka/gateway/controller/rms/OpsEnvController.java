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
package net.atayun.bazooka.gateway.controller.rms;

import net.atayun.bazooka.gateway.service.rms.OpsEnvService;
import net.atayun.bazooka.gateway.vo.rms.EnvVo;
import net.atayun.bazooka.rms.api.param.EnvCreateReq;
import net.atayun.bazooka.rms.api.param.EnvModifyReq;
import net.atayun.bazooka.rms.api.param.EnvQueryReq;
import com.youyu.common.api.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.youyu.common.api.Result.ok;

/**
 * @Author: hanxiaorui
 * @Date: 2019/7/25 10:37
 * @Description: 环境资源网关接口
 */
@Api(value = "环境资源网关接口", description = "环境资源网关接口", protocols = "http")
@RestController
@RequestMapping("/ata-ops/envs")
public class OpsEnvController {

    @Autowired
    private OpsEnvService envService;

    /**
     * 查询环境列表信息
     *
     * @param envQueryReq
     * @return
     */
    @ApiOperation(value = "查询环境列表信息")
    @PostMapping("/list")
    public Result<List<EnvVo>> list(@RequestBody @Valid EnvQueryReq envQueryReq) {
        return ok(envService.list(envQueryReq));
    }

    /**
     * 创建环境信息
     *
     * @param envCreateReq
     */
    @ApiOperation(value = "创建环境信息")
    @PostMapping("/create")
    public Result create(@RequestBody @Valid EnvCreateReq envCreateReq) {
        envService.create(envCreateReq);
        return ok();
    }

    /**
     * 更新环境信息
     *
     * @param envModifyReq
     */
    @ApiOperation(value = "更新环境信息")
    @PostMapping("/update")
    public Result update(@RequestBody @Valid EnvModifyReq envModifyReq) {
        envService.update(envModifyReq);
        return ok();
    }

    /**
     * 删除环境信息
     *
     * @param envId
     */
    @ApiOperation(value = "删除环境信息")
    @PostMapping("/{envId:\\d+}/delete")
    public Result delete(@PathVariable("envId") Long envId) {
        envService.delete(envId);
        return ok();
    }
}
