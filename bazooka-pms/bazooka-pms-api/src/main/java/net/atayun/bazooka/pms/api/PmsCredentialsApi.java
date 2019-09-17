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
package net.atayun.bazooka.pms.api;

import net.atayun.bazooka.pms.api.dto.PmsCredentialsDto;
import com.youyu.common.api.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author WangSongJun
 * @date 2019-08-27
 */
public interface PmsCredentialsApi {

    /**
     * 根据ID查询凭据详情
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID查询凭据详情")
    @PostMapping("/get/{id:\\d+}")
    Result<PmsCredentialsDto> getCredentialsDtoById(@PathVariable Long id);
}
