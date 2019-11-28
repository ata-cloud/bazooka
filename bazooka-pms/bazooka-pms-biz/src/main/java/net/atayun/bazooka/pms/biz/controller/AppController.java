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

import com.youyu.common.api.Result;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.enums.IsDeleted;
import com.youyu.common.exception.BizException;
import com.youyu.common.helper.YyRequestInfoHelper;
import com.youyu.common.utils.YyAssert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.atayun.bazooka.base.annotation.PmsAuth;
import net.atayun.bazooka.base.config.GitLabProperties;
import net.atayun.bazooka.base.enums.AppOptEnum;
import net.atayun.bazooka.base.git.GitServiceHelp;
import net.atayun.bazooka.pms.api.dto.*;
import net.atayun.bazooka.pms.api.enums.AppKindEnum;
import net.atayun.bazooka.pms.api.enums.ConfigTitleEnum;
import net.atayun.bazooka.pms.api.enums.CredentialTypeEnum;
import net.atayun.bazooka.pms.api.feign.AppApi;
import net.atayun.bazooka.pms.api.vo.AppEnvInfoVo;
import net.atayun.bazooka.pms.api.vo.AppInfoAddFormParam;
import net.atayun.bazooka.pms.api.vo.AppInfoUpdateFormParam;
import net.atayun.bazooka.pms.biz.dal.converter.AppDeployConfigConverter;
import net.atayun.bazooka.pms.biz.dal.converter.AppInfoConverter;
import net.atayun.bazooka.pms.biz.dal.entity.AppDeployConfigEntity;
import net.atayun.bazooka.pms.biz.dal.entity.AppInfoEntity;
import net.atayun.bazooka.pms.biz.dal.entity.PmsAppDeployStatusEntity;
import net.atayun.bazooka.pms.biz.dal.entity.PmsCredentialsEntity;
import net.atayun.bazooka.pms.biz.service.*;
import net.atayun.bazooka.rms.api.RmsDockerImageApi;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.dto.EnvDto;
import net.atayun.bazooka.rms.api.dto.*;
import net.atayun.bazooka.rms.api.param.EnvAppReq;
import net.atayun.bazooka.rms.api.param.EnvMlbPortUsedReq;
import net.atayun.bazooka.rms.api.param.EnvMlbPortUsedRsp;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author WangSongJun
 * @date 2019-07-16
 */
@Slf4j
@RestController
@RequestMapping("app")
@Api(description = "服务、服务发布配置相关接口")
public class AppController implements AppApi {
    @Autowired
    private AppService appService;
    @Autowired
    private AppInfoService appInfoService;
    @Autowired
    private AppDeployConfigService appDeployConfigService;
    @Autowired
    private SortService sortService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private PmsAppDeployStatusService appDeployStatusService;
    @Autowired
    private PmsServicePortDistributionService servicePortDistributionService;
    @Autowired(required = false)
    private GitLabApi gitLabApi;
    @Autowired
    private EnvApi envApi;
    @Autowired
    private RmsDockerImageApi dockerImageApi;
    @Autowired
    private PmsCredentialsService credentialsService;
    @Autowired
    private GitLabProperties gitLabProperties;
    @Autowired
    private GitServiceHelp gitServiceHelp;

    @Autowired
    @Lazy
    private ProjectController projectController;

    //region 服务信息

    @ApiOperation(value = "查询用户服务列表")
    @GetMapping("/list")
    public Result<List<AppInfoWithEnv>> getAppInfoListByUser(@RequestParam(required = false) Long projectId, @RequestParam(required = false) String keyword) {
        Long currentUserId = YyRequestInfoHelper.getCurrentUserId();
        List<AppInfoDto> appInfoListByUser = this.appService.getAppInfoListByUser(currentUserId, projectId, keyword);

        if (appInfoListByUser == null || appInfoListByUser.size() <= 0) {
            return Result.ok();
        }

        List<net.atayun.bazooka.pms.api.dto.EnvDto> pmsEnvList = new ArrayList<>();

        List<AppInfoWithEnv> appInfoWithEnvs = new ArrayList<>();

        for (int i = 0; i < appInfoListByUser.size(); i++) {
            if (i == 0) {
                pmsEnvList = projectController.queryEnvPortList(appInfoListByUser.get(i).getProjectId()).ifNotSuccessThrowException().getData();
            }
            if (i > 0 && !appInfoListByUser.get(i).getProjectId().equals(appInfoListByUser.get(i - 1).getProjectId())) {
                pmsEnvList.clear();
                pmsEnvList = projectController.queryEnvPortList(appInfoListByUser.get(i).getProjectId()).ifNotSuccessThrowException().getData();
            }

            List<AppEnvInfoVo> appEnvInfoVoList = new ArrayList<>();
            for (net.atayun.bazooka.pms.api.dto.EnvDto envDto : pmsEnvList) {
                AppEnvInfoVo appEnvInfoVo = getAppRuntimeInfo(appInfoListByUser.get(i).getId(), envDto.getEnvId()).getData();
                appEnvInfoVoList.add(appEnvInfoVo);
            }
            AppInfoWithEnv appInfoWithEnv = new AppInfoWithEnv(appInfoListByUser.get(i), appEnvInfoVoList);
            appInfoWithEnv.setId(appInfoListByUser.get(i).getId());
            appInfoWithEnvs.add(appInfoWithEnv);
        }

        return Result.ok(appInfoWithEnvs);
    }

    @PmsAuth
    @ApiOperation(value = "根据ID查询服务信息(前端使用，会验证用户权限)")
    @GetMapping("/get/{appId:\\d+}")
    public Result<AppInfoDto> getAppInfoByIdWithAuth(@PathVariable Long appId) {
        AppInfoDto appInfoDto = this.getAppInfoDtoIncludeProjectInfo(appId);
        Long currentUserId = YyRequestInfoHelper.getCurrentUserId();
        List<AppInfoDto> appInfoDtoList = new ArrayList<>(Arrays.asList(appInfoDto));
        appInfoDtoList = this.appService.setUserRoleType(currentUserId, appInfoDtoList);
        return Result.ok(appInfoDtoList.get(0));
    }

    @Override
    @ApiOperation(value = "根据ID查询服务信息(发布内部接口不需要验证)")
    @GetMapping("/_inner/get/{appId:\\d+}")
    public Result<AppInfoDto> getAppInfoById(@PathVariable Long appId) {

        return Result.ok(this.getAppInfoDtoIncludeProjectInfo(appId));
    }

    /**
     * 获取服务详情包括git库的凭证(内部接口不需要验证)
     *
     * @param appId
     * @return
     */
    @Override
    @ApiOperation(value = "获取服务详情包括git库的凭证(发布内部接口不需要验证)")
    @GetMapping("/_inner/get-include-credential/{appId:\\d+}")
    public Result<AppInfoWithCredential> getAppInfoWithCredentialById(@PathVariable Long appId) {
        AppInfoWithCredential appInfoWithCredential;
        AppInfoDto appInfoDto = this.getAppInfoDtoIncludeProjectInfo(appId);
        if (AppKindEnum.OPS_GITLAB.equals(appInfoDto.getAppKind())) {
            //获取一下托管gitlab 的账号
            appInfoWithCredential = new AppInfoWithCredential(appInfoDto, CredentialTypeEnum.USERNAME_WITH_PASSWORD, gitLabProperties.getUserName(), gitLabProperties.getPassWord());
        } else if (AppKindEnum.GIT_REPOSITORY.equals(appInfoDto.getAppKind())) {
            //查询git库的凭证信息
            PmsCredentialsDto credentialsDto = this.credentialsService.selectByPrimaryKey(appInfoDto.getGitCredentialId());
            appInfoWithCredential = new AppInfoWithCredential(appInfoDto, credentialsDto);
        } else {
            appInfoWithCredential = new AppInfoWithCredential(appInfoDto, null);
        }
        return Result.ok(appInfoWithCredential);

    }

    private AppInfoDto getAppInfoDtoIncludeProjectInfo(Long appId) {
        AppInfoDto appInfoDto = this.appInfoService.selectOne(new AppInfoEntity(appId, IsDeleted.NOT_DELETED));
        YyAssert.paramCheck(ObjectUtils.isEmpty(appInfoDto), "服务不存在或已删除！");
        ProjectInfoDto projectInfoDto = this.projectService.queryProjectById(appInfoDto.getProjectId());
        if (!ObjectUtils.isEmpty(projectInfoDto)) {
            appInfoDto.setProjectName(projectInfoDto.getProjectName());
            appInfoDto.setProjectId(projectInfoDto.getId());
        }
        return appInfoDto;
    }

    @ApiOperation(value = "新增服务")
    @PmsAuth(PmsAuth.AuthType.Write)
    @PostMapping("/create/{projectId:\\d+}")
    public Result<AppInfoDto> addAppInfo(@PathVariable Long projectId, @RequestBody @Validated AppInfoAddFormParam formParam) {
        AppInfoEntity appInfoEntity = AppInfoConverter.appInfoEntity(projectId, formParam);
        appInfoEntity = appService.addAppInfo(appInfoEntity);
        return Result.ok(appInfoService.convertEntityToDto(appInfoEntity));
    }

    @ApiOperation(value = "更新服务")
    @PmsAuth(PmsAuth.AuthType.Write)
    @PostMapping("/update/{appId:\\d+}")
    public Result<AppInfoDto> updateAppInfo(@PathVariable Long appId, @RequestBody @Validated AppInfoUpdateFormParam formParam) {
        AppInfoEntity appInfoEntity = AppInfoConverter.appInfoEntity(appId, formParam);
        return Result.ok(appService.updateAppInfo(appInfoEntity));
    }

    /**
     * 管理员或项目负责人才能删除服务（服务负责人不可以删除服务）
     *
     * @param appId
     * @return
     */
    @ApiOperation(value = "删除服务")
    @PmsAuth(PmsAuth.AuthType.Write)
    @PostMapping("/delete/{appId:\\d+}")
    public Result deleteAppInfo(@PathVariable Long appId) {
        return appService.deleteAppInfo(appId) > 0 ? Result.ok() : Result.fail("删除失败，请检查ID是否存在。");
    }


    @ApiOperation(value = "置顶")
    @PmsAuth
    @PostMapping("/top/add/{appId:\\d+}")
    public Result topProject(@PathVariable Long appId) {
        sortService.createTopConfig(YyRequestInfoHelper.getCurrentUserId(), appId, ConfigTitleEnum.APP_SORT);
        return Result.ok();
    }

    @ApiOperation(value = "取消置顶")
    @PmsAuth
    @PostMapping("/top/delete/{appId:\\d+}")
    public Result deleteTopProject(@PathVariable Long appId) {
        sortService.deleteConfig(YyRequestInfoHelper.getCurrentUserId(), appId, ConfigTitleEnum.APP_SORT);
        return Result.ok();
    }

    /**
     * 获取服务部署状态
     *
     * @param appId
     * @param envId
     * @return
     */
    @PmsAuth
    @ApiOperation("获取服务部署状态(前端使用)")
    @GetMapping("/deploy-status/get/{appId:\\d+}/{envId:\\d+}")
    public Result<PmsAppDeployStatusDto> getAppDeployStatusWithAuth(@PathVariable Long appId, @PathVariable Long envId) {

        return Result.ok(this.getAppDeployStatusDto(appId, envId));
    }

    /**
     * 获取服务部署状态
     *
     * @param appId
     * @param envId
     * @return
     */
    @Override
    @PmsAuth
    @ApiOperation("获取服务部署状态(发布内部使用)")
    @GetMapping("/_inner/deploy-status/get/{appId:\\d+}/{envId:\\d+}")
    public Result<PmsAppDeployStatusDto> getAppDeployStatus(@PathVariable Long appId, @PathVariable Long envId) {

        return Result.ok(this.getAppDeployStatusDto(appId, envId));
    }

    private PmsAppDeployStatusDto getAppDeployStatusDto(Long appId, Long envId) {
        PmsAppDeployStatusDto appDeployStatusDto = this.appDeployStatusService.selectOne(new PmsAppDeployStatusEntity(appId, envId));
        if (ObjectUtils.isEmpty(appDeployStatusDto)) {
            appDeployStatusDto = new PmsAppDeployStatusDto(appId, envId, false, null);
            this.appDeployStatusService.insertSelective(appDeployStatusDto);
        }
        return appDeployStatusDto;
    }

    /**
     * 更新服务部署状态
     *
     * @param appId
     * @param envId
     * @param appOperationEnum
     * @return
     */
    @Override
    @PostMapping("/deploy-status/update{appId:\\d+}/{envId:\\d+}")
    public Result<PmsAppDeployStatusDto> updateAppDeployStatus(@PathVariable Long appId, @PathVariable Long envId, @RequestParam boolean isDeploying, @RequestParam(required = false) AppOptEnum appOperationEnum) {
        PmsAppDeployStatusDto appDeployStatusDto = this.appDeployStatusService.selectOne(new PmsAppDeployStatusEntity(appId, envId));
        if (ObjectUtils.isEmpty(appDeployStatusDto)) {
            this.appDeployStatusService.insertSelective(new PmsAppDeployStatusEntity(appId, envId, isDeploying, appOperationEnum));
        } else {
            appDeployStatusDto.setDeploying(isDeploying);
            appDeployStatusDto.setDeployType(appOperationEnum);
            this.appDeployStatusService.updateByPrimaryKeySelective(appDeployStatusDto);
        }
        return Result.ok(appDeployStatusDto);
    }

    //endregion 服务信息


    //region 服务发布配置

    @ApiOperation(value = "查询服务所有的发布配置")
    @PmsAuth
    @GetMapping("/deploy-config/list/{appId:\\d+}")
    public Result<List<AppDeployConfigDto>> getAppDeployConfigList(@PathVariable Long appId, @RequestParam(required = false) Long envId, @RequestParam(required = false) String clusterType) {
        List<AppDeployConfigEntity> deployConfigEntityList = this.appDeployConfigService.selectEntity(new AppDeployConfigEntity(appId, envId, IsDeleted.NOT_DELETED, clusterType));
        if (ObjectUtils.isEmpty(deployConfigEntityList)) {
            return Result.ok();
        } else {
            //自己转dto
            List<AppDeployConfigDto> appDeployConfigDtos = deployConfigEntityList.stream()
                    .map(appDeployConfigEntity -> {
                        AppDeployConfigDto deployConfigDto = AppDeployConfigConverter.appDeployConfigDto(appDeployConfigEntity);
                        deployConfigDto.setEnvName(this.envApi.get(deployConfigDto.getEnvId()).ifNotSuccessThrowException().getData().getName());
                        return deployConfigDto;
                    })
                    .collect(Collectors.toList());
            return Result.ok(appDeployConfigDtos);
        }
    }


    @Override
    @ApiOperation(value = "根据ID查询服务发布配置")
    @GetMapping("/deploy-config/get/{configId:\\d+}")
    public Result<AppDeployConfigDto> getAppDeployConfigInfoById(@PathVariable Long configId) {
        AppDeployConfigEntity deployConfigEntity = this.appDeployConfigService.selectOneEntity(new AppDeployConfigEntity(configId, IsDeleted.NOT_DELETED));
        YyAssert.paramCheck(ObjectUtils.isEmpty(deployConfigEntity), "服务发布配置不存在或已删除！");
        AppDeployConfigDto deployConfigDto = AppDeployConfigConverter.appDeployConfigDto(deployConfigEntity);
        return Result.ok(deployConfigDto);
    }

    @ApiOperation(value = "新增服务发布配置")
    @PmsAuth(PmsAuth.AuthType.Write)
    @PostMapping("/deploy-config/create/{appId:\\d+}")
    public Result<AppDeployConfigDto> addAppDeployConfig(@PathVariable Long appId, @Validated @RequestBody AppDeployConfigDto deployConfigDto) {
        deployConfigDto.setAppId(appId);
        AppDeployConfigEntity deployConfigEntity = this.appService.saveDeployConfig(deployConfigDto);
        deployConfigDto = AppDeployConfigConverter.appDeployConfigDto(deployConfigEntity);
        return Result.ok(deployConfigDto);
    }

    @ApiOperation(value = "修改服务发布配置")
    @PmsAuth(PmsAuth.AuthType.Write)
    @PostMapping("/deploy-config/update/{appId:\\d+}/{configId:\\d+}")
    public Result<AppDeployConfigDto> updateAppDeployConfig(@PathVariable Long appId, @PathVariable Long configId, @Validated @RequestBody AppDeployConfigDto configDto) {
        configDto.setId(configId);
        configDto.setAppId(appId);
        AppDeployConfigEntity configEntity = this.appService.updateDeployConfig(configDto);
        configDto = AppDeployConfigConverter.appDeployConfigDto(configEntity);
        return Result.ok(configDto);
    }

    @ApiOperation(value = "删除服务发布配置")
    @PmsAuth(PmsAuth.AuthType.Write)
    @PostMapping("/deploy-config/delete/{appId:\\d+}/{configId:\\d+}")
    public Result deleteAppDeployConfig(@PathVariable Long appId, @PathVariable Long configId) {
        log.info("删除服务[{}]的发布配置[{}].", appId, configId);
        Example example = new Example(AppDeployConfigEntity.class);
        example.createCriteria()
                .andEqualTo("id", configId)
                .andEqualTo("appId", appId);
        return this.appDeployConfigService.updateByExampleSelective(new AppDeployConfigEntity(configId, IsDeleted.DELETED), example) > 0 ? Result.ok() : Result.fail("删除失败，请检查ID是否存在。");
    }


    @ApiOperation(value = "服务发布端口分配:申请")
    @PmsAuth(PmsAuth.AuthType.Write)
    @GetMapping("/deploy-config/service-port/get/{appId:\\d+}/{envId:\\d+}/{containerPort:\\d+}")
    public Result<Integer> appServicePortDistribution(@PathVariable Long appId, @PathVariable Long envId, @PathVariable Integer containerPort) {
        AppInfoEntity appInfoEntity = this.appInfoService.selectEntityByPrimaryKey(appId);
        YyAssert.paramCheck(ObjectUtils.isEmpty(appInfoEntity), "服务ID不存在！");
        Integer servicePort = this.servicePortDistributionService.distributionServicePort(appInfoEntity.getProjectId(), appId, envId, containerPort);
        return Result.ok(servicePort);
    }

    @ApiOperation(value = "验证服务端口是否可用")
    @GetMapping("/deploy-config/service-port/check/{appId:\\d+}/{envId:\\d+}/{containerPort:\\d+}/{servicePort:\\d+}")
    public Result servicePortCheck(@PathVariable Long appId, @PathVariable Long envId, @PathVariable Integer containerPort, @PathVariable Integer servicePort) {
        AppInfoEntity appInfoEntity = this.appInfoService.selectEntityByPrimaryKey(appId);
        YyAssert.paramCheck(ObjectUtils.isEmpty(appInfoEntity), "服务ID不存在！");
        this.servicePortDistributionService.servicePortCheck(appInfoEntity.getProjectId(), appId, envId, containerPort, servicePort);
        return Result.ok();
    }

    @ApiOperation(value = "验证MLB端口是否可用")
    @GetMapping("/deploy-config/mlb-port/check/{envId:\\d+}/{servicePort:\\d+}")
    public Result<EnvMlbPortUsedRsp> mlbPortCheck(@PathVariable Long envId, @PathVariable Integer servicePort) {
        return this.envApi.getEnvMlbPortUsedInfo(new EnvMlbPortUsedReq(envId, servicePort));
    }


    //endregion 服务发布配置

    @ApiOperation(value = "根据发布配置选择代码分支")
    @PmsAuth(PmsAuth.AuthType.Read)
    @GetMapping("/deploy/repository/branch/{appId:\\d+}/{configId:\\d+}")
    public Result<List<Branch>> getRepositoryBranchList(@PathVariable Long appId, @PathVariable Long configId) throws GitLabApiException {
        AppInfoEntity appInfoEntity = this.appInfoService.selectEntityByPrimaryKey(appId);
        YyAssert.paramCheck(ObjectUtils.isEmpty(appInfoEntity), "服务ID不存在！");

        AppDeployConfigEntity deployConfigEntity = this.appDeployConfigService.selectEntityByPrimaryKey(configId);
        YyAssert.paramCheck(ObjectUtils.isEmpty(deployConfigEntity), "服务发布配置ID不存在！");
        //获取所有分支信息
        List<Branch> branchList = null;

        if (AppKindEnum.OPS_GITLAB.equals(appInfoEntity.getAppKind())) {
            //ops托管gitlab
            branchList = gitLabApi.getRepositoryApi().getBranches(appInfoEntity.getGitlabId());
        } else if (AppKindEnum.GIT_REPOSITORY.equals(appInfoEntity.getAppKind())) {
            //非托管的git库
            Set<String> strBranchList;
            PmsCredentialsEntity credential = this.credentialsService.selectEntityByPrimaryKey(appInfoEntity.getGitCredentialId());
            if (ObjectUtils.isEmpty(credential)) {
                strBranchList = gitServiceHelp.branchList(appInfoEntity.getGitlabUrl(), "", "");
            } else {
                strBranchList = gitServiceHelp.branchList(appInfoEntity.getGitlabUrl(), credential.getCredentialKey(), credential.getCredentialValue());
            }
            if (!ObjectUtils.isEmpty(strBranchList)) {
                branchList = strBranchList.stream().map(branchName -> {
                    Branch branch = new Branch();
                    branch.setName(branchName);
                    return branch;
                }).collect(Collectors.toList());
            }
        } else {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "当前服务类型不存在代码分支信息！");
        }

        if (ObjectUtils.isEmpty(branchList)) {
            return Result.fail("没有获取到分支信息！");
        } else
            //按发布配置过滤分支信息
            if (StringUtils.hasText(deployConfigEntity.getGitBranchAllow())) {
                String branchRegex = deployConfigEntity.getGitBranchAllow().replaceAll("\\*", ".*");
                Pattern pattern = Pattern.compile(branchRegex);
                branchList = branchList.stream().filter(branch -> pattern.matcher(branch.getName()).matches()).collect(Collectors.toList());
            } else if (StringUtils.hasText(deployConfigEntity.getGitBranchDeny())) {
                String branchRegex = deployConfigEntity.getGitBranchDeny().replaceAll("\\*", ".*");
                Pattern pattern = Pattern.compile(branchRegex);
                branchList = branchList.stream().filter(branch -> !pattern.matcher(branch.getName()).matches()).collect(Collectors.toList());
            }
        return Result.ok(branchList);
    }


    //region 运行相关


    @ApiOperation(value = "服务概览，运行状态")
    @GetMapping("/run/status/{appId:\\d+}/{envId:\\d+}")
    public Result<AppEnvInfoVo> getAppRuntimeInfo(@PathVariable Long appId, @PathVariable Long envId) {
        AppInfoDto appInfoDto = this.getAppInfoDtoIncludeProjectInfo(appId);
        EnvDto envDto = this.envApi.get(envId).ifNotSuccessThrowException().getData();
        ClusterAppServiceInfoDto clusterAppServiceInfoDto = this.envApi.getClusterAppServiceInfo(new EnvAppReq(envId, appInfoDto.getDcosServiceId())).ifNotSuccessThrowException().getData();

        return Result.ok(new AppEnvInfoVo(
                appId,
                appInfoDto.getAppName(),
                appInfoDto.getProjectId(),
                appInfoDto.getProjectName(),
                appInfoDto.getGitlabUrl(),
                envId,
                envDto.getName(),
                envDto.getClusterId(),
                envDto.getClusterName(),
                clusterAppServiceInfoDto.getStatus(),
                clusterAppServiceInfoDto.getCpu(),
                clusterAppServiceInfoDto.getMemory(),
                clusterAppServiceInfoDto.getInstances()
        ));
    }


    @ApiOperation(value = "服务访问地址，端口")
    @GetMapping("/run/endpoint/{appId:\\d+}/{envId:\\d+}")
    public Result<List<ClusterAppServiceHostDto>> getAppEndpoint(@PathVariable Long appId, @PathVariable Long envId) {
        AppInfoDto appInfoDto = this.appInfoService.selectByPrimaryKey(appId);
        YyAssert.paramCheck(ObjectUtils.isEmpty(appInfoDto), "服务ID不存在！");

        Result<List<ClusterAppServiceHostDto>> clusterAppServiceHosts = this.envApi.getClusterAppServiceHosts(new EnvAppReq(envId, appInfoDto.getDcosServiceId()));

        return clusterAppServiceHosts;
    }


    @ApiOperation(value = "服务容器列表")
    @GetMapping("/run/containers/{appId:\\d+}/{envId:\\d+}")
    public Result<List<ClusterDockerDto>> getAppContainerList(@PathVariable Long appId, @PathVariable Long envId) {
        AppInfoDto appInfoDto = this.appInfoService.selectByPrimaryKey(appId);
        YyAssert.paramCheck(ObjectUtils.isEmpty(appInfoDto), "服务ID不存在！");

        Result<List<ClusterDockerDto>> listResult = this.envApi.getClusterDockers(new EnvAppReq(envId, appInfoDto.getDcosServiceId()));
        return listResult;
    }


    @ApiOperation(value = "服务当前镜像版本")
    @GetMapping("/run/current-image/{appId:\\d+}/{envId:\\d+}")
    public Result<RmsDockerImageDto> getAppCurrentImageInfo(@PathVariable Long appId, @PathVariable Long envId) {
        AppInfoDto appInfoDto = this.appInfoService.selectOne(new AppInfoEntity(appId, IsDeleted.NOT_DELETED));
        YyAssert.paramCheck(ObjectUtils.isEmpty(appInfoDto), "服务ID不存在或已删除！");
        String imageFullName = this.envApi.getClusterAppImage(new EnvAppReq(envId, appInfoDto.getDcosServiceId())).ifNotSuccessThrowException().getData();
        if (StringUtils.hasText(imageFullName)) {
            String registry = imageFullName.substring(0, imageFullName.indexOf("/"));
            String imageName = imageFullName.substring(imageFullName.indexOf("/") + 1, imageFullName.lastIndexOf(":"));
            String imageTag = imageFullName.substring(imageFullName.lastIndexOf(":") + 1);
            return this.dockerImageApi.getDockerImageInfoByImageName(registry, imageName, imageTag);
        } else {
            return Result.ok();
        }
    }

    //endregion

}
