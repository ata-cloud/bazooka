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
package net.atayun.bazooka.pms.biz.dal.converter;

import com.youyu.common.api.AppInfo;
import net.atayun.bazooka.pms.api.vo.AppInfoAddFormParam;
import net.atayun.bazooka.pms.api.vo.AppInfoUpdateFormParam;
import net.atayun.bazooka.pms.biz.dal.entity.AppInfoEntity;
import org.springframework.util.ObjectUtils;

/**
 * @author WangSongJun
 * @date 2019-07-16
 */
public class AppInfoConverter {

    public static AppInfo appInfo(Long projectId, AppInfoAddFormParam formParam) {
        return ObjectUtils.isEmpty(formParam)
                ? null
                : new AppInfoEntity(
                projectId,
                formParam.getAppName(),
                formParam.getAppCode(),
                formParam.getAppKind(),
                formParam.getDescription(),
                formParam.getLeaderId(),
                formParam.getGitUrl(),
                formParam.getGitCredentialId()
        );
    }

    public static AppInfoEntity appInfo(Long appId, AppInfoUpdateFormParam formParam) {
        return ObjectUtils.isEmpty(formParam)
                ? null
                : new AppInfoEntity(
                appId,
                null,
                formParam.getAppName(),
                null,
                null,
                formParam.getDescription(),
                formParam.getLeaderId()
        );
    }
}
