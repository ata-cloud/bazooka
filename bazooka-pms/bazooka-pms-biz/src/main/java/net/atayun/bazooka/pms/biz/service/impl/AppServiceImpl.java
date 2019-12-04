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
package net.atayun.bazooka.pms.biz.service.impl;

import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.enums.IsDeleted;
import com.youyu.common.exception.BizException;
import com.youyu.common.mapper.support.ExampleEnhancer;
import com.youyu.common.utils.YyAssert;
import lombok.extern.slf4j.Slf4j;
import net.atayun.bazooka.deploy.api.AppActionApi;
import net.atayun.bazooka.deploy.api.dto.AppRunningEventDto;
import net.atayun.bazooka.pms.api.dto.AppDeployConfigDto;
import net.atayun.bazooka.pms.api.dto.AppInfoDto;
import net.atayun.bazooka.pms.api.dto.PmsCredentialsDto;
import net.atayun.bazooka.pms.api.dto.ProjectInfoDto;
import net.atayun.bazooka.pms.api.enums.AppKindEnum;
import net.atayun.bazooka.pms.api.enums.UserTypeEnum;
import net.atayun.bazooka.pms.api.param.PortMapping;
import net.atayun.bazooka.pms.biz.dal.converter.AppDeployConfigConverter;
import net.atayun.bazooka.pms.biz.dal.entity.AppDeployConfigEntity;
import net.atayun.bazooka.pms.biz.dal.entity.AppInfoEntity;
import net.atayun.bazooka.pms.biz.dal.entity.PmsProjectEnvRelationEntity;
import net.atayun.bazooka.pms.biz.dal.entity.PmsUserProjectRelationEntity;
import net.atayun.bazooka.pms.biz.service.*;
import net.atayun.bazooka.rms.api.api.EnvApi;
import net.atayun.bazooka.rms.api.api.RmsClusterNodeApi;
import net.atayun.bazooka.rms.api.dto.ClusterAppServiceInfoDto;
import net.atayun.bazooka.rms.api.dto.EnvDto;
import net.atayun.bazooka.rms.api.dto.rsp.ClusterNodeRspDto;
import net.atayun.bazooka.rms.api.param.EnvAppReq;
import net.atayun.bazooka.upms.api.dto.rsp.UserDetailRspDTO;
import net.atayun.bazooka.upms.api.feign.UserApi;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Namespace;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.Visibility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.text.Collator;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.atayun.bazooka.pms.api.enums.PmsResultCode.GITLAB_CREATE_ERROR;

/**
 * @author WangSongJun
 * @date 2019-07-17
 */
@Slf4j
@Service
public class AppServiceImpl implements AppService {
    @Autowired
    private AppInfoService appInfoService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserApi userApi;
    @Autowired(required = false)
    private GitLabApi gitLabApi;
    @Autowired
    private SortService sortService;
    @Autowired
    private PmsUserProjectRelationService userProjectRelationService;
    @Autowired
    private PmsServicePortDistributionService servicePortDistributionService;
    @Autowired
    private AppDeployConfigService appDeployConfigService;
    @Autowired
    private AppActionApi appOperationEventApi;
    @Autowired
    private PmsCredentialsService credentialsService;
    @Autowired
    private EnvApi envApi;
    @Autowired
    private RmsClusterNodeApi clusterNodeApi;

    private final static Comparator<Object> CHINA_COMPARE = Collator.getInstance(java.util.Locale.CHINA);

    /**
     * 添加服务
     * <p>
     * 检查project是否存在
     * 验证project下app code唯一
     * 验证leaderId
     * 创建gitlab库，设置用户权限
     * 生成serviceId、imageName
     *
     * @param appInfoEntity
     */
    @Override
    public synchronized AppInfoEntity addAppInfo(AppInfoEntity appInfoEntity) {
        ProjectInfoDto projectInfoDto = this.projectService.queryProjectById(appInfoEntity.getProjectId());
        YyAssert.paramCheck(ObjectUtils.isEmpty(projectInfoDto), "项目ID不存在！");

        YyAssert.paramCheck(this.appInfoService.selectCount(new AppInfoEntity(appInfoEntity.getProjectId(), null, appInfoEntity.getAppName())) > 0, "同一项目下服务名称不能重复！");

        YyAssert.paramCheck(this.appInfoService.selectCount(new AppInfoEntity(appInfoEntity.getProjectId(), appInfoEntity.getAppCode(), null)) > 0, "同一项目下服务code不能重复！");

        UserDetailRspDTO userDetail = userApi.getUserDetail(appInfoEntity.getLeaderId()).ifNotSuccessThrowException().getData();
        YyAssert.paramCheck(ObjectUtils.isEmpty(userDetail), "服务负责人不存在！");

        //使用OPS托管代码库
        if (AppKindEnum.OPS_GITLAB.equals(appInfoEntity.getAppKind())) {
            try {
                Project gitlabProject = new Project();
                log.info("创建gitlab代码库，groupId：[{}]、appCode：[{}]", projectInfoDto.getGitlabGroupId(), appInfoEntity.getAppCode());
                gitlabProject.setVisibility(Visibility.PRIVATE);
                gitlabProject.setName(appInfoEntity.getAppCode());
                Namespace namespace = new Namespace();
                namespace.setId(projectInfoDto.getGitlabGroupId());
                gitlabProject.setNamespace(namespace);
                gitlabProject.setDescription(appInfoEntity.getDescription());
                gitlabProject = this.gitLabApi.getProjectApi().createProject(gitlabProject);
                appInfoEntity.setGitlabId(gitlabProject.getId());
                appInfoEntity.setGitlabUrl(gitlabProject.getWebUrl());
            } catch (GitLabApiException e) {
                throw new BizException(GITLAB_CREATE_ERROR.getCode(), GITLAB_CREATE_ERROR.getDesc(), e);
            }
        } else if (AppKindEnum.GIT_REPOSITORY.equals(appInfoEntity.getAppKind())) {
            if (!ObjectUtils.isEmpty(appInfoEntity.getGitCredentialId())) {
                PmsCredentialsDto pmsCredentialsDto = this.credentialsService.selectByPrimaryKey(appInfoEntity.getGitCredentialId());
                YyAssert.paramCheck(ObjectUtils.isEmpty(pmsCredentialsDto), "git凭据不存在！");
            }
        }

        appInfoEntity.setDcosServiceId(MessageFormat.format("/{0}/{1}", projectInfoDto.getProjectCode(), appInfoEntity.getAppCode()));
        appInfoEntity.setDockerImageName(MessageFormat.format("{0}/{1}", projectInfoDto.getProjectCode(), appInfoEntity.getAppCode()));
        YyAssert.isTrue(this.appInfoService.insertSelective(appInfoEntity) > 0, BaseResultCode.BUSINESS_ERROR.getCode(), "添加服务失败！");
        return appInfoEntity;
    }

    /**
     * 更新
     * <p>
     * 验证leaderId
     * 创建gitlab库，设置用户权限
     *
     * @param appInfoEntity
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppInfoDto updateAppInfo(AppInfoEntity appInfoEntity) {
        AppInfoDto oldAppInfoDto = this.appInfoService.selectByPrimaryKey(appInfoEntity.getId());
        YyAssert.paramCheck(ObjectUtils.isEmpty(oldAppInfoDto), "服务ID不存在！");

        //如果改了名字验证名字是否重复
        if (StringUtils.hasText(appInfoEntity.getAppName()) && !appInfoEntity.getAppName().equals(oldAppInfoDto.getAppName())) {
            appInfoEntity.setAppCode(oldAppInfoDto.getAppCode());
            YyAssert.paramCheck(this.appInfoService.selectCount(new AppInfoEntity(appInfoEntity.getProjectId(), null, appInfoEntity.getAppName())) > 0, "同一项目下服务名称不能重复！");

            oldAppInfoDto.setAppName(appInfoEntity.getAppName());
        }

        //验证负责人是否存在
        if (!ObjectUtils.isEmpty(appInfoEntity.getLeaderId()) && !appInfoEntity.getLeaderId().equals(oldAppInfoDto.getLeaderId())) {
            UserDetailRspDTO leaderInfo = userApi.getUserDetail(appInfoEntity.getLeaderId()).ifNotSuccessThrowException().getData();
            YyAssert.paramCheck(ObjectUtils.isEmpty(leaderInfo), "服务负责人不存在！");

            oldAppInfoDto.setLeaderId(appInfoEntity.getLeaderId());
        }

        //更新
        int updateCount = this.appInfoService.updateByPrimaryKeySelective(appInfoEntity);
        YyAssert.paramCheck(updateCount == 0, "没有要更新的内容！");

        if (StringUtils.hasText(appInfoEntity.getDescription())) {
            oldAppInfoDto.setDescription(appInfoEntity.getDescription());
        }
        return oldAppInfoDto;
    }

    /**
     * 根据当前用户查询服务列表
     *
     * @param currentUserId
     * @param projectId
     * @param keyword
     * @return
     */
    @Override
    public List<AppInfoDto> getAppInfoListByUser(Long currentUserId, Long projectId, String keyword) {
        //条件查询
        ExampleEnhancer example = new ExampleEnhancer(AppInfoEntity.class);
        example.orderBy("createTime").desc();
        Example.Criteria criteriaNotDeleteAndProjectId = example.createCriteria();
        criteriaNotDeleteAndProjectId.andEqualTo("isDeleted", IsDeleted.NOT_DELETED);
        if (!ObjectUtils.isEmpty(projectId)) {
            criteriaNotDeleteAndProjectId.andEqualTo("projectId", projectId);
        }

        if (StringUtils.hasText(keyword)) {
            example.and(
                    example.createCriteria()
                            .andLike("appName", MessageFormat.format("%{0}%", keyword))
                            .orLike("appCode", MessageFormat.format("%{0}%", keyword))
                            .orLike("description", MessageFormat.format("%{0}%", keyword))
            );
        }
        List<AppInfoDto> appInfoDtoList = this.appInfoService.selectByExample(example);
        if (ObjectUtils.isEmpty(appInfoDtoList)) {
            return null;
        }

        //权限验证
        if (!userApi.isAdmin(currentUserId).ifNotSuccessThrowException().getData()) {
            //设置用户角色
            appInfoDtoList = this.setUserRoleType(currentUserId, appInfoDtoList);
            //过滤掉没有权限的服务
            appInfoDtoList = appInfoDtoList.stream().filter(appInfoDto -> appInfoDto.getUserType() != null).collect(Collectors.toList());
        }

        //置顶排序
        if (!ObjectUtils.isEmpty(appInfoDtoList)) {
            //添加项目名称
            appInfoDtoList = appInfoDtoList.stream().map(appInfoDto -> {
                ProjectInfoDto projectInfoDto = this.projectService.queryProjectById(appInfoDto.getProjectId());
                if (!ObjectUtils.isEmpty(projectInfoDto)) {
                    appInfoDto.setProjectName(projectInfoDto.getProjectName());
                    return appInfoDto;
                } else {
                    return null;
                }
            }).collect(Collectors.toList());
            appInfoDtoList.removeAll(Collections.singleton(null));

            Map<String, List<AppInfoDto>> projectAppMap = appInfoDtoList.stream().collect(Collectors.groupingBy(AppInfoDto::getProjectName));

            //根据项目、服务名排序
            List<AppInfoDto> appDtoList = new ArrayList<>(16);
            projectAppMap.entrySet()
                    .stream().sorted(Comparator.comparing(Map.Entry::getKey, CHINA_COMPARE))
                    .forEach(entry -> entry.getValue().stream().sorted(Comparator.comparing(AppInfoDto::getAppName, CHINA_COMPARE)).forEach(appInfoDto -> appDtoList.add(appInfoDto)));
            //根据置顶排序
            appInfoDtoList = this.sortService.appListTopOrder(appDtoList, currentUserId);
        }


        return appInfoDtoList;
    }

    @Override
    public List<AppInfoDto> setUserRoleType(Long userId, List<AppInfoDto> appInfoDtoList) {
        List<PmsUserProjectRelationEntity> userProjectRelationEntityList = this.userProjectRelationService.selectEntity(new PmsUserProjectRelationEntity(null, userId, null));

        appInfoDtoList = appInfoDtoList.stream()
                .map(appInfoDto -> {
                    //项目负责人或参与人
                    if (!ObjectUtils.isEmpty(userProjectRelationEntityList)) {
                        userProjectRelationEntityList.stream()
                                .filter(pmsUserProjectRelationEntity -> pmsUserProjectRelationEntity.getProjectId().equals(appInfoDto.getProjectId()))
                                .forEach(pmsUserProjectRelationEntity -> {
                                    if (UserTypeEnum.USER_PROJECT_MASTER.equals(pmsUserProjectRelationEntity.getRoleType())
                                            || ObjectUtils.isEmpty(appInfoDto.getUserType())) {
                                        appInfoDto.setUserType(pmsUserProjectRelationEntity.getRoleType());
                                    }
                                });

                    }
                    //服务负责人
                    if (userId.equals(appInfoDto.getLeaderId()) && !UserTypeEnum.USER_PROJECT_MASTER.equals(appInfoDto.getUserType())) {
                        appInfoDto.setUserType(UserTypeEnum.USER_APP_MASTER);
                    }
                    return appInfoDto;
                }).collect(Collectors.toList());

        return appInfoDtoList;

    }

    /**
     * 删除服务
     * <p>
     * 同时删除 gitlab代码库、容器、镜像、发布配置
     * 8、服务删除为软删除，页面不再显示此服务。不删除gitlab，容器个数设为0，镜像不删除。同一项目下新建服务时，不可以和已删除的服务名或者CODE重复
     *
     * @param appId
     * @return
     */
    @Override
    public int deleteAppInfo(Long appId) {
        //检查服务状态是否可以删除
        this.checkAppStatusForDelete(appId);
        //删除服务
        int count = this.appInfoService.updateByPrimaryKeySelective(new AppInfoEntity(appId, IsDeleted.DELETED));
        return count;
    }

    /**
     * 检查服务状态是否可以删除（不能删除正在运行或发布的服务）
     *
     * @param appId
     * @return
     */
    @Override
    public boolean checkAppStatusForDelete(Long appId) {
        log.info("检查服务是否正在运行或发布,appId:[{}]", appId);

        AppInfoDto appInfoDto = this.appInfoService.selectByPrimaryKey(appId);
        YyAssert.paramCheck(ObjectUtils.isEmpty(appInfoDto) || IsDeleted.DELETED.equals(appInfoDto.getIsDeleted()), MessageFormat.format("服务[{0}]不存在或已删除！", appId));

        log.info("检查服务当前是否正在发布或进行相关事件操作");
        List<AppRunningEventDto> appEventList = appOperationEventApi.getAppRunningEvent(appId).ifNotSuccessThrowException().getData();
        if (!ObjectUtils.isEmpty(appEventList)) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, MessageFormat.format("无法删除运行中或正在进行发布相关事件的服务，当前服务状态：{0}", appEventList.get(0).getEvent().getDescription()));
        }

        log.info("检查服务是否正在运行");
        List<PmsProjectEnvRelationEntity> pmsProjectEnvRelationEntities = this.projectService.queryProjectEnvInfo(appInfoDto.getProjectId());
        if (!ObjectUtils.isEmpty(pmsProjectEnvRelationEntities)) {
            pmsProjectEnvRelationEntities.stream()
                    .forEach(projectEnv -> {
                        ClusterAppServiceInfoDto appServiceInfoDto = this.envApi.getClusterAppServiceInfo(new EnvAppReq(projectEnv.getEnvId(), appInfoDto.getDcosServiceId(), appId)).ifNotSuccessThrowException().getData();
                        //状态 0:未发布 1:已关闭 2:启动中 3:运行中
                        String appStatus = appServiceInfoDto.getStatus();
                        YyAssert.paramCheck("2".equals(appStatus), "服务正在启动中，不能删除！");
                        YyAssert.paramCheck("3".equals(appStatus), "服务正在运行中，不能删除！");
                    });
        }
        return true;
    }

    /**
     * MLB 端口检测
     *
     * @param appId
     * @param envId
     * @param port
     * @return
     */
    @Override
    public boolean checkMlbPort(Long appId, Long envId, Integer port) {
        AppInfoDto appInfoDto = this.appInfoService.selectByPrimaryKey(appId);
        YyAssert.paramCheck(ObjectUtils.isEmpty(appInfoDto), "服务ID不存在！");
        PmsProjectEnvRelationEntity projectEnvInfo = this.projectService.queryProjectEnvInfo(appInfoDto.getProjectId(), envId);
        YyAssert.paramCheck(ObjectUtils.isEmpty(projectEnvInfo), "服务对应环境下没有端口分配，检测参数是否正确！");

        YyAssert.paramCheck(port < projectEnvInfo.getPortStart() || port > projectEnvInfo.getPortEnd(), MessageFormat.format("端口：{0} 不在分配的范围[{1},{2}]内！", port, projectEnvInfo.getPortStart(), projectEnvInfo.getPortEnd()));

        return true;
    }

    private static Pattern DISABLED_COMMANDS_PATTERN = Pattern.compile("(\\brm\\b\\s)|(\\breboot\\b)|(\\bshutdown\\b)");

    /**
     * 保存发布配置
     *
     * @param deployConfigDto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized AppDeployConfigEntity saveDeployConfig(AppDeployConfigDto deployConfigDto) {
        //参数验证
        this.deployConfigCheck(deployConfigDto);

        int count = this.appDeployConfigService.selectCount(new AppDeployConfigEntity(deployConfigDto.getAppId(), deployConfigDto.getEnvId(), deployConfigDto.getConfigName(), IsDeleted.NOT_DELETED));
        YyAssert.paramCheck(count > 0, "同一服务环境下配置名称不可重复！");

        AppInfoDto appInfoDto = this.appInfoService.selectByPrimaryKey(deployConfigDto.getAppId());

        AppDeployConfigEntity deployConfigEntity = AppDeployConfigConverter.appDeployConfigEntity(deployConfigDto);
        this.appDeployConfigService.insertSelective(deployConfigEntity);

        //记录服务端口分配信息
        deployConfigDto.getPortMappings()
                .stream()
                .filter(portMapping -> !ObjectUtils.isEmpty(portMapping.getServicePort()))
                .forEach(portMapping -> this.servicePortDistributionService.saveServicePort(appInfoDto.getProjectId(), appInfoDto.getId(), deployConfigDto.getEnvId(), deployConfigEntity.getId(), portMapping.getContainerPort(), portMapping.getServicePort()));

        return deployConfigEntity;
    }

    /**
     * 更新发布配置
     *
     * @param deployConfigDto
     * @return
     */
    @Override
    public AppDeployConfigEntity updateDeployConfig(AppDeployConfigDto deployConfigDto) {
        this.deployConfigCheck(deployConfigDto);

        Example checkConfigName = new Example(AppDeployConfigEntity.class);
        checkConfigName.createCriteria()
                .andEqualTo("appId", deployConfigDto.getAppId())
                .andEqualTo("envId", deployConfigDto.getEnvId())
                .andEqualTo("configName", deployConfigDto.getConfigName())
                .andEqualTo("isDeleted", IsDeleted.NOT_DELETED)
                .andNotEqualTo("id", deployConfigDto.getId());

        List<AppDeployConfigDto> appDeployConfigDtoList = this.appDeployConfigService.selectByExample(checkConfigName);
        YyAssert.paramCheck(!ObjectUtils.isEmpty(appDeployConfigDtoList), "同一服务环境下配置名称不可重复！");

        AppInfoDto appInfoDto = this.appInfoService.selectByPrimaryKey(deployConfigDto.getAppId());

        deployConfigDto.getPortMappings()
                .stream()
                .filter(portMapping -> !ObjectUtils.isEmpty(portMapping.getServicePort()))
                .forEach(portMapping -> this.servicePortDistributionService.saveServicePort(appInfoDto.getProjectId(), appInfoDto.getId(), deployConfigDto.getEnvId(), deployConfigDto.getId(), portMapping.getContainerPort(), portMapping.getServicePort()));

        AppDeployConfigEntity deployConfigEntity = AppDeployConfigConverter.appDeployConfigEntity(deployConfigDto);

        Example example = new Example(AppDeployConfigEntity.class);
        example.createCriteria()
                .andEqualTo("id", deployConfigDto.getId())
                .andEqualTo("appId", deployConfigDto.getAppId());
        this.appDeployConfigService.updateByExample(deployConfigEntity, example);
        return deployConfigEntity;
    }


    /**
     * 验证参数、端口、环境、集群、节点
     *
     * @param deployConfigDto
     */
    private void deployConfigCheck(AppDeployConfigDto deployConfigDto) {
        log.info("验证发布配置参数：端口、环境、集群、节点");

        //验证参数：编译命令禁止 rm、shutdown、reboot；
        if (StringUtils.hasText(deployConfigDto.getCompileCommand())) {
            YyAssert.paramCheck(DISABLED_COMMANDS_PATTERN.matcher(deployConfigDto.getCompileCommand()).find(), "编译命令禁止包含 rm、shutdown、reboot!");
        }
        //验证参数：Dockerfile 路径禁止 rm、shutdown、reboot；
        if (StringUtils.hasText(deployConfigDto.getDockerfilePath())) {
            YyAssert.paramCheck(DISABLED_COMMANDS_PATTERN.matcher(deployConfigDto.getDockerfilePath()).find(), "Dockerfile 路径不合法！禁止包含 rm、shutdown、reboot!");
        }

        //端口映射
        List<PortMapping> portMappings = deployConfigDto.getPortMappings();
        portMappings = portMappings.stream().filter(portMapping -> !ObjectUtils.isEmpty(portMapping)).collect(Collectors.toList());
        deployConfigDto.setPortMappings(portMappings);

        List<Integer> containerPosts = portMappings.stream().filter(portMapping -> portMapping.getContainerPort() != null).map(PortMapping::getContainerPort).collect(Collectors.toList());
        YyAssert.paramCheck(containerPosts.size() > containerPosts.stream().distinct().count(), "容器端口不能重复！");
        List<Integer> servicePorts = portMappings.stream().filter(portMapping -> portMapping.getServicePort() != null).map(PortMapping::getServicePort).collect(Collectors.toList());
        YyAssert.paramCheck(servicePorts.size() > servicePorts.stream().distinct().count(), "服务端口不能重复！");

        //验证环境是否存在
        EnvDto envDto = this.envApi.get(deployConfigDto.getEnvId()).ifNotSuccessThrowException().getData();
        log.info("envDto:{}", envDto);

        YyAssert.paramCheck(!envDto.getClusterType().equals(deployConfigDto.getClusterType()), "集群类型不符！");

        //bazooka 单节点发布，必须选择节点、节点必须在集群的范围
        YyAssert.paramCheck("2".equals(deployConfigDto.getClusterType()) && ObjectUtils.isEmpty(deployConfigDto.getClusterNodes()), "Bazooka单节点集群发布必须选择发布节点信息！");
        YyAssert.paramCheck("2".equals(deployConfigDto.getClusterType()) && deployConfigDto.getInstance() != deployConfigDto.getClusterNodes().size(), "实例个数(" + deployConfigDto.getInstance() + ")与所选节点数(" + deployConfigDto.getClusterNodes().size() + ")不符！");

        if ("2".equals(deployConfigDto.getClusterType()) && !ObjectUtils.isEmpty(deployConfigDto.getClusterNodes())) {

            List<ClusterNodeRspDto> clusterNodeDtoList = clusterNodeApi.getAllClusterNodes(envDto.getClusterId()).ifNotSuccessThrowException().getData();
            YyAssert.paramCheck(ObjectUtils.isEmpty(clusterNodeDtoList), "环境下没有可用节点！");

            List<Long> nodeIdList = clusterNodeDtoList.stream().map(node -> node.getId()).collect(Collectors.toList());
            deployConfigDto.getClusterNodes().forEach(nodeId -> {
                YyAssert.paramCheck(!nodeIdList.contains(nodeId), "所选节点信息[id:" + nodeId + "]不存在！");
            });
        }

    }
}
