package net.atayun.bazooka.deploy.biz.v2.controller;

import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import net.atayun.bazooka.deploy.api.AppActionApi;
import net.atayun.bazooka.deploy.api.dto.AppRunningEventDto;
import net.atayun.bazooka.deploy.biz.v2.dto.app.AppEventOperateWithStatusDto;
import net.atayun.bazooka.deploy.biz.v2.dto.app.AppOptHisMarathonDto;
import net.atayun.bazooka.deploy.biz.v2.dto.app.AppOptLogDto;
import net.atayun.bazooka.deploy.biz.v2.dto.app.AppActionDto;
import net.atayun.bazooka.deploy.biz.v2.dto.app.AppOptHisDto;
import net.atayun.bazooka.deploy.biz.v2.param.AppActionParam;
import net.atayun.bazooka.deploy.biz.v2.param.AppOptHisMarathonParam;
import net.atayun.bazooka.deploy.biz.v2.param.AppOptHisParam;
import net.atayun.bazooka.deploy.biz.v2.service.app.AppActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Ping
 */
@RestController
public class AppActionController implements AppActionApi {

    @Autowired
    private AppActionService appActionService;

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
    public Result<AppActionDto> action(@Validated @RequestBody AppActionParam appActionParam) {
        AppActionDto appActionDto = appActionService.action(appActionParam);
        return Result.ok(appActionDto);
    }

    @Override
    public Result<List<AppRunningEventDto>> getAppRunningEvent(@PathVariable("appId") Long appId) {
        List<AppRunningEventDto> events = appActionService.getAppRunningEvent(appId);
        return Result.ok(events);
    }

    @ApiOperation("所有操作记录")
    @PostMapping("/operate/history")
    public Result<PageData<AppOptHisDto>> getAppOptHis(@Validated @RequestBody AppOptHisParam pageParam) {
        PageData<AppOptHisDto> pageData = appActionService.getAppOptHis(pageParam);
        return Result.ok(pageData);
    }

    @ApiOperation("所有操作记录(Marathon相关)")
    @PostMapping("/operate/history/marathon")
    public Result<PageData<AppOptHisMarathonDto>> getAppOptHistMarathon(@Validated @RequestBody AppOptHisMarathonParam pageParam) {
        PageData<AppOptHisMarathonDto> pageData = appActionService.getAppOptHisMarathon(pageParam);
        return Result.ok(pageData);
    }

    @ApiOperation("操作记录详情(Marathon相关)")
    @ApiImplicitParam(name = "optId", value = "事件Id", required = true, dataType = "long", paramType = "path")
    @GetMapping("/operate/history/marathon/detail/{optId}")
    public Result<String> getAppOptHisMarathonDetail(@PathVariable("optId") Long optId) {
        String marathonConfig = appActionService.getAppOptHisMarathonDetail(optId);
        return Result.ok(marathonConfig);
    }

    @ApiOperation("操作记录详情")
    @ApiImplicitParam(name = "optId", value = "事件Id", required = true, dataType = "long", paramType = "path")
    @GetMapping("/operate/log/{optId}")
    public Result<List<AppOptLogDto>> getAppOptLog(@PathVariable("optId") Long optId) {
        List<AppOptLogDto> list = appActionService.getAppOptLog(optId);
        return Result.ok(list);
    }

    @ApiOperation("操作记录状态")
    @ApiImplicitParam(name = "optId", value = "事件Id", required = true, dataType = "long", paramType = "path")
    @GetMapping("/operate/status/{optId}")
    public Result<AppEventOperateWithStatusDto> getOptStatus(@PathVariable("optId") Long optId) {
        AppEventOperateWithStatusDto dto = appActionService.getOptStatus(optId);
        return Result.ok(dto);
    }

}
