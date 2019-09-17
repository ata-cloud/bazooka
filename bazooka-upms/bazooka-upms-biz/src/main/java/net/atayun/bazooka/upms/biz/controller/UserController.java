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
package net.atayun.bazooka.upms.biz.controller;

import net.atayun.bazooka.upms.api.dto.req.*;
import net.atayun.bazooka.upms.api.dto.rsp.UserDetailRspDTO;
import net.atayun.bazooka.upms.api.dto.rsp.UserLoginRspDTO;
import net.atayun.bazooka.upms.api.dto.rsp.UserMenuPermissionRspDTO;
import net.atayun.bazooka.upms.api.dto.rsp.UserQueryRspDTO;
import net.atayun.bazooka.upms.api.feign.UserApi;
import net.atayun.bazooka.upms.biz.service.UserService;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.youyu.common.api.Result.ok;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 用户controller
 */
@RestController
@RequestMapping(value = "/user")
public class UserController implements UserApi {

    @Autowired
    private UserService userService;

    @Override
    @PostMapping("/login")
    public Result<UserLoginRspDTO> login(@Valid @RequestBody UserLoginReqDTO userLoginReqDTO) {
        return ok(userService.login(userLoginReqDTO));
    }

    @Override
    @PostMapping("/logout")
    public Result logout() {
        userService.logout();
        return ok();
    }

    @Override
    @PostMapping("/modifyPassword")
    public Result modifyPassword(@Valid @RequestBody UserModifyPasswordReqDTO userModifyPasswordReqDTO) {
        userService.modifyPassword(userModifyPasswordReqDTO);
        return ok();
    }

    @Override
    @PostMapping("/add")
    public Result<Long> add(@Valid @RequestBody UserAddReqDTO userAddReqDTO) {
        Long userId = userService.add(userAddReqDTO);
        return ok(userId);
    }

    @Override
    @PostMapping("/delete")
    public Result delete(@Valid @RequestBody UserDeleteReqDTO userDeleteReqDTO) {
        userService.delete(userDeleteReqDTO);
        return ok();
    }

    @Override
    @PostMapping("/edit")
    public Result edit(@Valid @RequestBody UserEditReqDTO userEditReqDTO) {
        userService.edit(userEditReqDTO);
        return ok();
    }

    @Override
    @PostMapping("/getPage")
    public Result<PageData<UserQueryRspDTO>> getPage(@RequestBody UserQueryReqDTO userQueryReqDTO) {
        return ok(userService.getPage(userQueryReqDTO));
    }

    @Override
    @GetMapping("/unauthorized")
    public Result unauthorized() {
        userService.unauthorized();
        return ok();
    }

    @Override
    @GetMapping("/needLogin")
    public Result needLogin() {
        userService.needLogin();
        return ok();
    }

    @Override
    @PostMapping("/getUserMenuPermissions")
    public Result<List<UserMenuPermissionRspDTO>> getUserMenuPermissions(@RequestBody UserMenuPermissionReqDTO userMenuPermissionReqDTO) {
        return ok(userService.getUserMenuPermissions(userMenuPermissionReqDTO));
    }

    @Override
    @PostMapping("/getUserDetail")
    public Result<UserDetailRspDTO> getUserDetail(@RequestBody Long userId) {
        return ok(userService.getUserDetail(userId));
    }

    @Override
    @PostMapping("/isAdmin")
    public Result<Boolean> isAdmin(@RequestBody(required = false) Long userId) {
        return ok(userService.isAdmin(userId));
    }
}
