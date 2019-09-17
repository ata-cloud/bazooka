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
package net.atayun.bazooka.deploy.api;

import net.atayun.bazooka.deploy.api.dto.AppEventOperateDto;
import net.atayun.bazooka.deploy.api.dto.AppRunningEventDto;
import net.atayun.bazooka.deploy.api.param.AppOperationEventParam;
import net.atayun.bazooka.deploy.api.param.MarathonCallbackParam;
import com.youyu.common.api.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Ping
 * @date 2019-07-26
 */
@Api(description = "服务操作接口")
@RequestMapping("/deploy/app")
public interface AppOperationEventApi {

    /**
     * operate
     *
     * @param appOperationEventParam appOperationEventParam
     * @return result
     */
    @ApiOperation(value = "事件操作",
            notes = "事件类型:\n" +
                    "1. 启动服务: START\n" +
                    "2. 关闭服务: STOP\n" +
                    "3. 重启服务: RESTART\n" +
                    "4. 扩/缩容服务: SCALE\n" +
                    "5. 服务发布: DEPLOY\n" +
                    "6. 服务回滚: ROLLBACK\n" +
                    "7. 推送镜像: PUSH_IMAGE\n" +
                    "8. 删除镜像: DELETE_IMAGE\n\n" +
                    "关于detail参数详解:\n" +
                    "1. 启动服务: {\"appId\": 1, \"envId\": 1, \"instance\": 1}\n" +
                    "2. 关闭服务: {\"appId\": 1, \"envId\": 1}\n" +
                    "3. 重启服务: {\"appId\": 1, \"envId\": 1}\n" +
                    "4. 扩/缩容服务: {\"appId\": 1, \"envId\": 1, \"instance\": 1, " +
                    "\"cpu\": 1.0, \"memory\": 1.0, \"disk\": 1.0}\n" +
                    "5. 服务发布: {\"appId\": 1, \"envId\": 1, \"deployConfigId\": 1, \"branch\": \"dev\", " +
                    "\"dockerImageTag\": \"test_dev_201908051019\"}\n" +
                    "6. 服务回滚: {\"appId\": 1, \"envId\": 1, \"templateEventId\": 1}\n" +
                    "7. 推送镜像: {\"appId\": 1, \"envId\": 1, \"imageId\": 1, \"dockerImageTag\": \"test_dev_201908051019\"," +
                    "\"isExternalDockerRegistry\": true, \"targetEnvId\": 2, \"targetDockerRegistry\": \"xxx\"," +
                    "\"needAuth\": false, \"credentialId\": 1}\n" +
                    "8. 删除镜像: {\"appId\": 1, \"envId\": 1, \"imageId\": 1, \"dockerImageTag\": \"test_dev_201908051019\"}\n" +
                    "\n" +
                    "字段详解:\n" +
                    "1. appId: 服务ID\n" +
                    "2. envId: 服务所在环境ID\n" +
                    "3. instance: 服务启动的实例个数\n" +
                    "4. cpu: 服务所需cpu shares\n" +
                    "5. memory: 服务所需内存大小\n" +
                    "6. disk: 服务所需磁盘容量\n" +
                    "7. deployConfigId: 服务发布配置ID\n" +
                    "8. branch: 构建发布所选择的分支(镜像发布时该参数不需要传入)\n" +
                    "9. dockerImageTag: 镜像发布所选择的镜像Tag(构建发布时该参数不需要传入)\n" +
                    "10. templateEventId: 服务回滚的模板事件Id\n" +
                    "11. isExternalDockerRegistry: 是否是外部镜像库\n" +
                    "12. targetEnvId: 目标环境(isExternalDockerRegistry == false 时传入)\n" +
                    "13. targetDockerRegistry: 目标镜像库(isExternalDockerRegistry == true 时传入)\n" +
                    "14. needAuth: 目标镜像库是否需要登录\n" +
                    "15. username: 用户名(needAuth == true 时传入)\n" +
                    "16. password: 密码(needAuth == true 时传入)\n" +
                    "17. imageId: 镜像ID\n"
    )
    @PostMapping("/operate")
    Result<AppEventOperateDto> operate(@Validated @RequestBody AppOperationEventParam appOperationEventParam);

    /**
     * 处理marathon回调
     *
     * @param marathonCallbackParam 参数
     * @return result
     */
    @ApiOperation(value = "处理marathon回调")
    @PostMapping("/marathon/callback")
    Result marathonCallback(@Validated @RequestBody MarathonCallbackParam marathonCallbackParam);

    /**
     * 服务正在运行的事件
     *
     * @param appId appId
     * @return AppRunningEventDto
     */
    @ApiOperation(value = "服务正在运行的事件")
    @GetMapping("/app-running-event/{appId}")
    Result<List<AppRunningEventDto>> getAppRunningEvent(@PathVariable("appId") Long appId);
}
