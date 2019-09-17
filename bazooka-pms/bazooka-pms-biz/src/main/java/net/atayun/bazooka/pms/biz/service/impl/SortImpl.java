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

import com.alibaba.fastjson.JSONObject;
import net.atayun.bazooka.pms.api.dto.AppInfoDto;
import net.atayun.bazooka.pms.api.dto.PmsUserConfigDto;
import net.atayun.bazooka.pms.api.dto.UserSortDto;
import net.atayun.bazooka.pms.api.enums.ConfigTitleEnum;
import net.atayun.bazooka.pms.api.vo.ProjectShowResponse;
import net.atayun.bazooka.pms.biz.dal.entity.PmsUserConfigEntity;
import net.atayun.bazooka.pms.biz.service.PmsUserConfigService;
import net.atayun.bazooka.pms.biz.service.SortService;
import com.youyu.common.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.atayun.bazooka.pms.api.enums.PmsResultCode.PMS_TOP_ERROR;

/**
 * @author rache
 * @date 2019-07-18
 */
@Service
public class SortImpl implements SortService {

    @Autowired
    private PmsUserConfigService pmsUserConfigMapper;

    /**
     * 获取并解析排序配置
     * @param userId
     * @param configTitle
     * @return
     */
       private List<UserSortDto> toppingConfig(long userId, ConfigTitleEnum configTitle){
            PmsUserConfigEntity config=findConfig(userId,configTitle);
            //没有配置返回null
            if(config==null||config.getConfigBody()==null||config.getConfigBody().equals("")){
                return null;
            }

            List<UserSortDto> sortDtos= JSONObject.parseArray(config.getConfigBody(),UserSortDto.class);
            return sortDtos;
        }

    /**
     * 配置表查询
     * @param userId
     * @param title
     * @return
     */
        private PmsUserConfigEntity findConfig(long userId, ConfigTitleEnum title){
            PmsUserConfigEntity entity=new PmsUserConfigEntity();
            entity.setUserId(userId);
            entity.setConfigTitle(title);
            return pmsUserConfigMapper.selectOneEntity(entity);
        }

    /**
     * 应用列表置顶排序
     *
     * @param appInfoDtoList
     * @param userId
     * @return
     */
    @Override
    public List<AppInfoDto> appListTopOrder(List<AppInfoDto> appInfoDtoList, long userId) {
        List<UserSortDto> userSortDtoList = this.toppingConfig(userId, ConfigTitleEnum.APP_SORT);
        if (userSortDtoList != null) {
            for (AppInfoDto appInfoDto : appInfoDtoList) {
                Optional<UserSortDto> userSortDto = userSortDtoList.stream().filter(m -> m.getSortId().equals(appInfoDto.getId())).findFirst();
                if (userSortDto.isPresent()) {
                    appInfoDto.setOrderId(userSortDto.get().getOrderId());
                }
            }
            appInfoDtoList = appInfoDtoList.stream().sorted(Comparator.comparing(AppInfoDto::getOrderId).reversed()).collect(Collectors.toList());
        }

        return appInfoDtoList;
    }

    /**
     * 项目置顶
     * @param projectList
     * @param userId
     * @param configTitle
     * @return
     */
    @Override
        public List<ProjectShowResponse> topProject(List<ProjectShowResponse> projectList, long userId, ConfigTitleEnum configTitle){
            List<UserSortDto> userSortDtos=toppingConfig(userId, configTitle);
            if(userSortDtos!=null) {
                for(ProjectShowResponse showResponse:projectList){
                    Optional<UserSortDto> userSortDto=  userSortDtos.stream().filter(m->m.getSortId().equals(showResponse.getProjectId())).findFirst();
                    if(userSortDto.isPresent()) {
                        showResponse.setOrderId(userSortDto.get().getOrderId());
                    }
                }
            }

            projectList= projectList.stream().sorted(
                    Comparator.comparing(ProjectShowResponse::getOrderId).reversed().
                    thenComparing(ProjectShowResponse::getProjectName))
                    .collect(Collectors.toList());
            return projectList;
        }

    /**
     * 创建项目置顶的配置json
     * @param userId
     * @param projectId
     * @param configTitle
     */
        @Override
        public void createTopConfig(Long userId,Long projectId,ConfigTitleEnum configTitle){
            List<UserSortDto> userSortDtos=toppingConfig(userId, configTitle);
            if(userSortDtos!=null&&userSortDtos.size()>0){
                int maxCount=userSortDtos.stream().max(Comparator.comparing(UserSortDto::getOrderId)).get().getOrderId();

                boolean isExist=false;
                for(UserSortDto item :userSortDtos){
                    if(item.getSortId().equals(projectId))
                    {
                        isExist=true;
                        item.setOrderId(maxCount+1);
                        break;
                    }
                }
                //判断当前配置是否已经存在
                if(!isExist){
                    UserSortDto intoDto=new UserSortDto();
                    intoDto.setOrderId(maxCount+1);
                    intoDto.setSortId(projectId);
                    userSortDtos.add(intoDto);
                }
                String configBody=JSONObject.toJSONString(userSortDtos);
                pmsUserConfigMapper.updateConfig(userId,configTitle,configBody);
            }else {
                //新建置顶json
                List<UserSortDto> userSortList=new ArrayList<>();
                UserSortDto newUserSort=new UserSortDto();
                newUserSort.setSortId(projectId);
                newUserSort.setOrderId(1);
                userSortList.add(newUserSort);
                String configBody=JSONObject.toJSONString(userSortList);
                //新建用户配置
                PmsUserConfigEntity entity=new PmsUserConfigEntity();
                entity.setConfigTitle(configTitle);
                entity.setUserId(userId);
                entity.setConfigBody(configBody);
                pmsUserConfigMapper.insert(entity);
            }

        }

    /**
     * 删除项目置顶的配置
     * @param userId
     * @param projectId
     * @param configTitle
     */
        @Override
        public void deleteConfig(Long userId,Long projectId,ConfigTitleEnum configTitle) {
            List<UserSortDto> userSortDtos = toppingConfig(userId, configTitle);
            if (userSortDtos != null && userSortDtos.size() > 0) {
                boolean isExist = false;
                for (UserSortDto item : userSortDtos) {
                    if (item.getSortId().equals(projectId)) {
                        userSortDtos.remove(item);
                        isExist = true;
                        break;
                    }
                }
                if(userSortDtos.size()==0) {
                    PmsUserConfigDto entity=new PmsUserConfigDto();
                    entity.setUserId(userId);
                    entity.setConfigTitle(configTitle);
                    pmsUserConfigMapper.delete(entity);
                    return;
                }
                if(!isExist){
                    throw new BizException(PMS_TOP_ERROR.getCode(),PMS_TOP_ERROR.getDesc());
                }
                String configBody=JSONObject.toJSONString(userSortDtos);
                pmsUserConfigMapper.updateConfig(userId,configTitle,configBody);
            }else {
                throw new BizException(PMS_TOP_ERROR.getCode(),PMS_TOP_ERROR.getDesc());
            }
        }
}
