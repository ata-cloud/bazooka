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
package net.atayun.bazooka.pms.biz.controller;

import net.atayun.bazooka.combase.annotation.PmsAuth;
import net.atayun.bazooka.combase.page.PageQuery;
import net.atayun.bazooka.pms.api.ProjectApi;
import net.atayun.bazooka.pms.api.EnvDto;
import net.atayun.bazooka.pms.api.dto.PmsProjectInfoDto;
import net.atayun.bazooka.pms.api.dto.ProjectCountDto;
import net.atayun.bazooka.pms.api.dto.ProjectInfoDto;
import net.atayun.bazooka.pms.api.enums.ConfigTitleEnum;
import net.atayun.bazooka.pms.api.enums.UserTypeEnum;
import net.atayun.bazooka.pms.biz.dal.entity.PmsProjectEnvRelationEntity;
import net.atayun.bazooka.pms.biz.dal.entity.PmsUserProjectRelationEntity;
import net.atayun.bazooka.pms.biz.service.*;
import net.atayun.bazooka.pms.api.api.EnvApi;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import com.youyu.common.exception.BizException;
import com.youyu.common.helper.YyRequestInfoHelper;
import com.youyu.common.transfer.BaseBeanUtils;
import com.youyu.common.utils.YyBeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.atayun.bazooka.pms.api.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static net.atayun.bazooka.pms.api.enums.PmsResultCode.NOT_PROJECT;


/**
 * @author rache
 * @date 2019/7/17
 */
@Slf4j
@Api(value = "项目管理接口", description = "对项目的CRUD操作", protocols = "http")
@RestController
@Validated
public class ProjectController implements ProjectApi {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private SortService sortService;
    @Autowired
    private PmsUserProjectRelationService pmsUserProjectRelationService;
    @Autowired
    private PmsProjectInfoService pmsProjectInfoService;
    @Autowired
    private PmsGitlabUserService pmsGitlabUserService;
    @Autowired
    private EnvApi envApi;

    @ApiOperation(value = "获取项目列表")
    @PostMapping("/project/queryProjectList")
    public Result<List<ProjectShowResponse>> queryProjectList(@RequestBody ProjectQueryReq req){
        List<ProjectCountDto> projectCountDtos=projectService.queryProjectCount(YyRequestInfoHelper.getCurrentUserId(),req.getKeyword());
        //获取项目统计数据
        List<ProjectShowResponse> data= BaseBeanUtils.copy(projectCountDtos,ProjectShowResponse.class);
        //置顶再处理
        data=sortService.topProject(data,YyRequestInfoHelper.getCurrentUserId(), ConfigTitleEnum.PROJECT_SORT);
        return Result.ok(data);
    }

    @ApiOperation(value = "获取负责项目列表")
    @PostMapping("/project/queryProjectListForAdmin")
    public Result<List<PmsProjectInfoDto>> queryProjectListForAdmin(){
        List<PmsProjectInfoDto> dtos=  projectService.queryProjectListForAdmin(YyRequestInfoHelper.getCurrentUserId());
        return Result.ok(dtos);
    }

    @ApiOperation(value = "创建项目")
    @PostMapping("/project/createProject")
    public Result createProject(@RequestBody @Valid ProjectRequest projectRequest){
        projectService.createProjectForWeb(projectRequest);
        return Result.ok();
    }
    @ApiOperation(value = "更新项目")
    @PostMapping("/project/updateProject/{projectId}")
    @PmsAuth(PmsAuth.AuthType.Write)
    public Result<ProjectRequest> updateProject(@PathVariable Long projectId,@RequestBody @Valid UpdateProjectReq projectRequest){
        projectRequest.setProjectId(projectId);
        projectService.updateProjectForWeb(projectRequest);
        return Result.ok();
    }
    @ApiOperation(value = "删除项目")
    @PostMapping("/project/deleteProject/{projectId}")
    @PmsAuth(PmsAuth.AuthType.Write)
    public Result deleteProject(@PathVariable Long projectId){
        projectService.deleteProjectNotReal(projectId);
        return Result.ok();
    }
    @ApiOperation(value = "项目发布统计")
    @PostMapping("/project/deployCount")
    public Result deployCount(@RequestBody DeployCountRequest deployCountRequest){
        return Result.ok();
    }

    @PmsAuth(PmsAuth.AuthType.Write)
    @Override
    @ApiOperation(value = "项目参与人列表")
    @PostMapping("/project/queryDevUser/{projectId}")
    public Result<PageData<DevUserResponse>> queryDevUser(@PathVariable Long projectId, @RequestBody PageQuery pageQuery){
        PageData<DevUserResponse> userListEntity= pmsUserProjectRelationService.queryUserForProject(pageQuery,projectId, UserTypeEnum.USER_PROJECT_DEV);

        return Result.ok(userListEntity);
    }


    @PmsAuth(PmsAuth.AuthType.Write)
    @ApiOperation(value = "添加项目参与人")
    @PostMapping("/project/addDevUser/{projectId}")
    public Result addDevUser(@PathVariable Long projectId,@RequestBody DevUserRequest request){
       if(request.getUserIds()!=null&&request.getUserIds().size()>0){
           ProjectInfoDto pjd= pmsProjectInfoService.queryProjectById(projectId);
           if(pjd==null){
               throw new BizException(NOT_PROJECT.getCode(),NOT_PROJECT.getDesc());
           }
           projectService.bathInsertDev(request.getUserIds(),projectId,pjd.getGitlabGroupId());
           return Result.ok();
       }
       return Result.fail("参与人为空");
    }

    @PmsAuth(PmsAuth.AuthType.Write)
    @ApiOperation(value = "删除项目参与人")
    @PostMapping("/project/deleteDevUser/{projectId}")
    public Result deleteDevUser(@PathVariable Long projectId,@RequestBody DevUserRequest request){
        pmsUserProjectRelationService.deleteUserForProject(request.getUserId(),projectId,UserTypeEnum.USER_PROJECT_DEV);
        return Result.ok();
    }

    @ApiOperation(value = "创建项目置顶")
    @PostMapping("/project/topProject/{projectId}")
    public Result topProject(@PathVariable Long projectId) {
        sortService.createTopConfig(YyRequestInfoHelper.getCurrentUserId(),projectId,ConfigTitleEnum.PROJECT_SORT);
        return Result.ok();
    }
    @ApiOperation(value = "取消项目置顶")
    @PostMapping("/project/deleteTopProject/{projectId}")
    public Result deleteTopProject(@PathVariable Long projectId) {
        sortService.deleteConfig(YyRequestInfoHelper.getCurrentUserId(),projectId,ConfigTitleEnum.PROJECT_SORT);
        return Result.ok();
    }

    @Override
    @ApiOperation(value = "获取项目各个环境端口信息")
    @PostMapping("/project/queryEnvPortList/{projectId}")
    public Result<List<EnvDto>> queryEnvPortList(@PathVariable Long projectId) {
        List<PmsProjectEnvRelationEntity> entityList= projectService.queryProjectEnvInfo(projectId);
        List<EnvDto> envDtos =BaseBeanUtils.copy(entityList, EnvDto.class);
        return Result.ok(envDtos);
    }

    @ApiOperation(value = "根据环境获取项目数量")
    @PostMapping("/project/countProject/{envId}")
    @Override
    public Result<Integer> countProject(@PathVariable Long envId) {
        List<PmsProjectEnvRelationEntity> entityList= projectService.queryProjectByEnvNotDelete(envId);
        return Result.ok(entityList.size());
    }

    @ApiOperation(value = "根据环境获取可使用端口")
    @PostMapping("/project/queryDistributePort/{envId}")
    public Result<EnvDto> queryDistributePort(@PathVariable Long envId) {
        EnvDto envDto= envApi.get(envId).ifNotSuccessThrowException().getData();
        EnvDto dto= projectService.queryDistributePort(envId,envDto.getClusterId());
        return Result.ok(dto);
    }


    @Override
    @PmsAuth(PmsAuth.AuthType.Read)
    @ApiOperation(value = "获取项目信息")
    @PostMapping("/project/queryProjectInfo/{projectId}")
    public Result<ProjectResponse> queryProjectInfo(@PathVariable Long projectId) {
        ProjectInfoDto dto= projectService.queryProjectById(projectId);
        PmsUserProjectRelationEntity relations= pmsUserProjectRelationService.queryProjectMaster(projectId);
        ProjectResponse response=new ProjectResponse();
        if(dto!=null){
            YyBeanUtils.copyProperties(dto,response);
            if(relations!=null){
                response.setMasterUserId(relations.getUserId());
            }
        }
        UserTypeEnum adminUserRole= projectService.queryUserInProject(projectId,YyRequestInfoHelper.getCurrentUserId());
        response.setAdminUserRole(adminUserRole);
        return Result.ok(response);
    }


    @Override
    @ApiOperation(value = "添加gitlab用户")
    @PostMapping("/project/addGitlabUser")
    public Result addGitlabUser(@RequestBody GitlabUserRequest request) {
        pmsGitlabUserService.addGitlabUser(request.getUserId(),request.getEmail(),request.getUserName());
        return Result.ok();
    }

    @Override
    @ApiOperation(value = "获取用户所拥有环境")
    @PostMapping("/project/queryEnvForUser/{userId}")
    public Result<List<Long>> queryEnvForUser(@PathVariable Long userId) {
        List<Long> date= pmsUserProjectRelationService.queryEnvForUser(userId);
        return Result.ok(date);
    }
}
