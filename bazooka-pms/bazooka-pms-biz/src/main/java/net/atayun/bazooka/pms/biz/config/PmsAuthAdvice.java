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
package net.atayun.bazooka.pms.biz.config;

import net.atayun.bazooka.base.annotation.PmsAuth;
import net.atayun.bazooka.pms.api.dto.AppInfoDto;
import net.atayun.bazooka.pms.api.enums.UserTypeEnum;
import net.atayun.bazooka.pms.biz.dal.entity.AppInfoEntity;
import net.atayun.bazooka.pms.biz.dal.entity.PmsUserProjectRelationEntity;
import net.atayun.bazooka.pms.biz.service.AppInfoService;
import net.atayun.bazooka.pms.biz.service.PmsUserProjectRelationService;
import net.atayun.bazooka.upms.api.feign.UserApi;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.enums.IsDeleted;
import com.youyu.common.exception.BizException;
import com.youyu.common.helper.YyRequestInfoHelper;
import com.youyu.common.utils.YyAssert;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author WangSongJun
 * @date 2019-07-01
 */
@Slf4j
@Aspect
@Component
public class PmsAuthAdvice {
    @Autowired
    private AppInfoService appInfoService;
    @Autowired
    private PmsUserProjectRelationService userProjectRelationService;
    @Autowired
    private UserApi userApi;

    /**
     * pms 数据权限验证
     * <p>
     * 需要验证数据权限的方法上添加 @PmsAuth 注解,appId 或projectId应存在 pathVariables 中
     * 应用和项目角色验证
     *
     * @param joinPoint
     */
    @Before("@annotation(net.atayun.bazooka.base.annotation.PmsAuth)")
    public void validate(JoinPoint joinPoint) {
        //当前用户ID
        Long currentUserId = YyRequestInfoHelper.getCurrentUserId();
        YyAssert.isTrue(!ObjectUtils.isEmpty(currentUserId), "403", "用户未登录！");
        log.info("PMS 数据权限验证 userId:[{}]", currentUserId);
        if (userApi.isAdmin(currentUserId).ifNotSuccessThrowException().getData()) {
            log.info("用户是管理员！");
            return;
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        log.info("进行 @PmsAuth 权限验证：[{}]", request.getRequestURL());
        //目标节点的ID
        Long appId = ((Map<String, Long>) request.getAttribute(View.PATH_VARIABLES)).get("appId");

        Long projectId = ((Map<String, Long>) request.getAttribute(View.PATH_VARIABLES)).get("projectId");

        YyAssert.paramCheck(ObjectUtils.isEmpty(appId) && ObjectUtils.isEmpty(projectId), "pathVariables 没有 projectId 或 appId！");
        // 获取目标接口的权限验证类型
        Class targetClass = joinPoint.getTarget().getClass();
        String methodName = joinPoint.getSignature().getName();
        Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();
        PmsAuth.AuthType authType = this.getAuthType(targetClass, methodName, parameterTypes);
        log.info("authType:[{}]", authType);

        //进行权限验证
        if (!ObjectUtils.isEmpty(projectId)) {
            log.info("项目级权限验证--> projectId:[{}]", projectId);
            boolean projectPermission = PmsAuth.AuthType.Write.equals(authType)
                    ? this.userProjectRelationService.selectCount(new PmsUserProjectRelationEntity(projectId, currentUserId, UserTypeEnum.USER_PROJECT_MASTER)) > 0
                    : this.userProjectRelationService.selectCount(new PmsUserProjectRelationEntity(projectId, currentUserId, null)) > 0;
            YyAssert.isTrue(projectPermission, "当前用户没有项目[" + projectId + "]的[" + authType + "]权限。");
        } else {
            log.info("应用级权限验证--> appId:[{}]", appId);
            boolean appPermission = false;
            AppInfoDto appInfoDto = this.appInfoService.selectOne(new AppInfoEntity(appId, IsDeleted.NOT_DELETED));
            YyAssert.paramCheck(ObjectUtils.isEmpty(appInfoDto), "应用ID[" + appId + "]不存在！");
            if (currentUserId.equals(appInfoDto.getLeaderId())
                    || this.userProjectRelationService.selectCount(new PmsUserProjectRelationEntity(appInfoDto.getProjectId(), currentUserId, UserTypeEnum.USER_PROJECT_MASTER)) > 0) {
                appPermission = true;
            } else if (PmsAuth.AuthType.Read.equals(authType)
                    && this.userProjectRelationService.selectCount(new PmsUserProjectRelationEntity(appInfoDto.getProjectId(), currentUserId, UserTypeEnum.USER_PROJECT_DEV)) > 0) {
                appPermission = true;
            }
            YyAssert.isTrue(appPermission, "当前用户没有应用[" + appId + "]的[" + authType + "]权限。");
        }
        log.info("@PmsAuth 权限验证验证通过！");
    }

    /**
     * 获取方法上 PmsAuth 中的 AuthType (需要认证的权限类型)
     *
     * @param targetClass
     * @param methodName
     * @param parameterTypes
     * @return
     */
    private PmsAuth.AuthType getAuthType(Class targetClass, String methodName, Class<?>[] parameterTypes) {
        try {
            Method method = targetClass.getMethod(methodName, parameterTypes);
            PmsAuth.AuthType authType = method.getAnnotation(PmsAuth.class).value();
            return authType;
        } catch (NoSuchMethodException e) {
            throw new BizException(BaseResultCode.SYSTEM_ERROR, e);
        }
    }
}
