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
package net.atayun.bazooka.upms.biz.component.filter;

import net.atayun.bazooka.combase.subject.dto.OpsUserDto;
import net.atayun.bazooka.upms.biz.component.properties.ShiroProperties;
import net.atayun.bazooka.upms.biz.domain.User;
import net.atayun.bazooka.upms.biz.helper.header.OpsServletRequestHelper;
import net.atayun.bazooka.upms.biz.service.PermissionService;
import net.atayun.bazooka.upms.biz.service.UserService;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static net.atayun.bazooka.combase.subject.OpsThreadLocalSubject.removeOpsUser;
import static net.atayun.bazooka.combase.subject.OpsThreadLocalSubject.setOpsUser;
import static net.atayun.bazooka.combase.utils.OrikaCopyUtil.copyProperty;
import static net.atayun.bazooka.combase.utils.StringUtil.eq;
import static com.youyu.common.constant.RequestHeaderConst.REAL_NAME_HEADER;
import static com.youyu.common.constant.RequestHeaderConst.USER_ID_HEADER;
import static java.lang.String.valueOf;
import static java.util.Objects.isNull;
import static org.apache.shiro.SecurityUtils.getSubject;
import static org.apache.shiro.web.util.WebUtils.issueRedirect;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work shiro 请求url匹配过滤器
 */
public class ShiroUrlPathMatchingFilter extends PathMatchingFilter {

    @Autowired
    private UserService userService;
    @Autowired
    private PermissionService permissionService;

    @Autowired
    private ShiroProperties shiroProperties;

    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        if (isValidate(request)) {
            issueRedirect(request, response, shiroProperties.getUnauthorized());
            return false;
        }

        return true;
    }

    @Override
    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        User user = (User) getSubject().getPrincipal();
        if (isNull(user)) {
            super.doFilterInternal(request, response, chain);
            return;
        }

        Map<String, String> opsHeaderMap = getOpsHeaderMap(user);
        OpsServletRequestHelper opsServletRequestHelper = new OpsServletRequestHelper((HttpServletRequest) request, opsHeaderMap);
        try {
            setOpsUser(copyProperty(user, OpsUserDto.class));
            super.doFilterInternal(opsServletRequestHelper, response, chain);
        } finally {
            removeOpsUser();
        }
    }

    /**
     * 设置Ops header头部信息
     *
     * @param user
     * @return
     */
    private Map<String, String> getOpsHeaderMap(User user) {
        Map<String, String> opsHeaderMap = new HashMap<>();
        opsHeaderMap.put(USER_ID_HEADER, valueOf(user.getUserId()));
        opsHeaderMap.put(REAL_NAME_HEADER, user.getRealName());
        return opsHeaderMap;
    }

    /**
     * 是否需要验证
     *
     * @param request
     * @return
     */
    private boolean isValidate(ServletRequest request) {
        Subject subject = getSubject();
        if (!subject.isAuthenticated()) {
            return false;
        }

        String requestUrl = getPathWithinApplication(request);
        if (eq(shiroProperties.getUnauthorized(), requestUrl)) {
            return false;
        }

        boolean existFlag = permissionService.isUrlExist(requestUrl);
        if (!existFlag) {
            return false;
        }

        User user = (User) subject.getPrincipal();
        boolean userHasFlag = userService.hasUrlPermission(user.getId(), requestUrl);
        if (userHasFlag) {
            return false;
        }

        return true;
    }
}
