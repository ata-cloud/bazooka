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

import net.atayun.bazooka.pms.api.PmsCredentialsApi;
import net.atayun.bazooka.pms.api.dto.PmsCredentialsDto;
import net.atayun.bazooka.pms.api.enums.CredentialDomainEnum;
import net.atayun.bazooka.pms.api.vo.CredentialsUpdateParam;
import net.atayun.bazooka.pms.biz.dal.entity.PmsCredentialsEntity;
import net.atayun.bazooka.pms.biz.service.PmsCredentialsService;
import com.youyu.common.api.Result;
import com.youyu.common.utils.YyAssert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 凭据管理
 *
 * @author WangSongJun
 * @date 2019-08-27
 */
@Slf4j
@RestController
@RequestMapping("credentials")
@Api(description = "凭据管理")
public class CredentialsController implements PmsCredentialsApi {
    @Autowired
    private PmsCredentialsService credentialsService;

    /**
     * 根据ID查询凭据详情
     *
     * @param id
     * @return
     */
    @Override
    @ApiOperation(value = "根据ID查询凭据详情")
    @PostMapping("/get/{id:\\d+}")
    public Result<PmsCredentialsDto> getCredentialsDtoById(@PathVariable Long id) {
        PmsCredentialsDto credentialsDto = this.credentialsService.selectByPrimaryKey(id);
        return Result.ok(credentialsDto);
    }

    @GetMapping("/get/list")
    @ApiOperation(value = "查询凭据列表")
    public Result<List<PmsCredentialsDto>> getCredentials(@RequestParam(required = false) CredentialDomainEnum domain) {
        List<PmsCredentialsDto> credentialsDtoList = this.credentialsService.select(new PmsCredentialsEntity(domain));
        if (!ObjectUtils.isEmpty(credentialsDtoList)) {
            credentialsDtoList = credentialsDtoList.stream()
                    .map(credentialsDto -> {
                        credentialsDto.setCredentialValue("******");
                        return credentialsDto;
                    }).collect(Collectors.toList());
        }
        return Result.ok(credentialsDtoList);
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加凭据")
    public Result<PmsCredentialsDto> addCredential(@RequestBody @Validated PmsCredentialsDto credentialsDto) {
        credentialsDto.setId(null);
        YyAssert.paramCheck(this.credentialsService.selectCount(new PmsCredentialsEntity(credentialsDto.getCredentialName())) > 0, "凭据名称不可重复！");
        int result = this.credentialsService.insertSelective(credentialsDto);
        return result > 0 ? Result.ok(credentialsDto) : Result.fail("添加失败！");
    }

    @PostMapping("/update/{id:\\d+}")
    @ApiOperation(value = "修改凭据")
    public Result<PmsCredentialsDto> updateCredential(@PathVariable Long id, @RequestBody @Validated CredentialsUpdateParam updateParam) {
        int result = this.credentialsService.updateByPrimaryKeySelective(new PmsCredentialsEntity(id, updateParam.getCredentialKey(), updateParam.getCredentialValue()));
        return result > 0 ? this.getCredentialsDtoById(id) : Result.fail("修改凭据失败！");
    }

}
