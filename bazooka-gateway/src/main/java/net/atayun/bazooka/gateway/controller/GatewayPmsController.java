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
package net.atayun.bazooka.gateway.controller;

import net.atayun.bazooka.combase.GitLabProperties;
import net.atayun.bazooka.combase.page.PageQuery;
import net.atayun.bazooka.gateway.service.rms.OpsEnvService;
import net.atayun.bazooka.gateway.vo.Pms.ProjectDetailResponse;
import net.atayun.bazooka.gateway.vo.rms.EnvVo;
import net.atayun.bazooka.pms.api.ProjectApi;
import net.atayun.bazooka.pms.api.EnvDto;
import net.atayun.bazooka.pms.api.vo.DevUserResponse;
import net.atayun.bazooka.pms.api.vo.ProjectResponse;
import net.atayun.bazooka.pms.api.param.EnvQueryReq;
import net.atayun.bazooka.upms.api.dto.req.UserAddReqDTO;
import net.atayun.bazooka.upms.api.dto.req.UserQueryReqDTO;
import net.atayun.bazooka.upms.api.dto.rsp.UserDetailRspDTO;
import net.atayun.bazooka.upms.api.dto.rsp.UserQueryRspDTO;
import net.atayun.bazooka.upms.api.feign.UserApi;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import com.youyu.common.transfer.BaseBeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.youyu.common.api.Result.ok;

/**
 * @author rache
 * @date 2019-07-30
 */
@Api(value = "环境资源网关接口", description = "环境资源网关接口", protocols = "http")
@RestController
@RequestMapping("/gateWay-pms")
public class GatewayPmsController {
    @Autowired
    private UserApi userApi;
    @Autowired
    private ProjectApi projectApi;
    @Autowired
    private OpsEnvService envService;
    @Autowired
    private GitLabProperties gitLabProperties;
    /**
     * 添加用户
     *
     * @param
     * @return
     */
    @ApiOperation(value = "添加用户")
    @PostMapping("/addUser")
    public Result addUser(@Valid @RequestBody UserAddReqDTO userAddReqDTO) {
        Long userId= userApi.add(userAddReqDTO).getData();
        /*GitlabUserRequest request=new GitlabUserRequest();
        request.setEmail(userAddReqDTO.getEmail());
        request.setUserId(userId);
        request.setUserName(userAddReqDTO.getUsername());
        projectApi.addGitlabUser(request);*/
        return ok();
    }

    @ApiOperation(value = "根据项目id获取包含环境")
    @PostMapping("/queryEnvInfoByProjectId/{projectId}")
    public Result queryEnvInfoByProjectId(@PathVariable Long projectId) {
        EnvQueryReq envQueryReq=new EnvQueryReq();
        List<EnvVo> envVos= envService.list(envQueryReq);
        List<EnvDto> pmsEnvList= projectApi.queryEnvPortList(projectId).ifNotSuccessThrowException().getData();
        for(EnvDto item:pmsEnvList)
        {
           Optional<EnvVo> envVo=  envVos.stream().filter(m->m.getId().equals(item.getEnvId())).findFirst();
           if(envVo.isPresent()) {
               item.setEnvName(envVo.get().getName());
           }
        }
        return ok(pmsEnvList);
    }

    @ApiOperation(value = "项目参与人列表")
    @PostMapping("/project/queryDevUser/{projectId}")
    public Result<PageData<UserQueryRspDTO>> queryDevUser(@PathVariable Long projectId, @RequestBody PageQuery query){
        PageData pmsPage= projectApi.queryDevUser(projectId,query).ifNotSuccessThrowException().getData();
        List<DevUserResponse> pmsDevList=pmsPage.getRows();
        if(pmsDevList.size()>0) {
           UserQueryReqDTO userQueryReqDTO=new UserQueryReqDTO();
           List<Long> userIds=pmsDevList.stream().map(m->m.getUserId()).collect(Collectors.toList());
           userQueryReqDTO.setUserIds(userIds);
           userQueryReqDTO.setPageNo(0);
           userQueryReqDTO.setPageSize(100);
           List<UserQueryRspDTO> userInfoPage=  getAllUser(userQueryReqDTO);
           pmsPage.setRows(userInfoPage);
           return Result.ok(pmsPage);
        }
        return Result.ok();
    }

    @ApiOperation(value = "判断是否存在自建gitlab")
    @GetMapping("/project/isAtaGitlab")
    public Result<Boolean> isAtaGitlab(){

        return Result.ok(gitLabProperties.getAtaGitlab());
    }
    private List<UserQueryRspDTO> getAllUser(UserQueryReqDTO userQueryReqDTO){
        List<UserQueryRspDTO> userQueryRspDTOS=new ArrayList<>();
        PageData<UserQueryRspDTO> userInfoPage= userApi.getPage(userQueryReqDTO).ifNotSuccessThrowException().getData();
        userQueryRspDTOS.addAll(userInfoPage.getRows());
        if(userInfoPage.getTotalPage()>1){
            for (int i=0;i<(userInfoPage.getTotalPage()-1);i++){
                PageData<UserQueryRspDTO> itemPage= userApi.getPage(userQueryReqDTO).ifNotSuccessThrowException().getData();
                userQueryRspDTOS.addAll(itemPage.getRows());
            }
        }
        return userQueryRspDTOS;
    }

    @ApiOperation(value = "根据项目id获取信息")
    @PostMapping("/queryProjectInfo/{projectId}")
    public Result<ProjectDetailResponse> queryProjectInfo(@PathVariable Long projectId) {
        ProjectResponse projectResponse= projectApi.queryProjectInfo(projectId).ifNotSuccessThrowException().getData();
        ProjectDetailResponse response= BaseBeanUtils.copy(projectResponse,ProjectDetailResponse.class);
        if(response.getMasterUserId()!=null) {
           UserDetailRspDTO userDetailRspDTO= userApi.getUserDetail(response.getMasterUserId()).ifNotSuccessThrowException().getData();
           response.setMasterRealName(userDetailRspDTO.getRealName());
        }
        return ok(response);
    }
}
