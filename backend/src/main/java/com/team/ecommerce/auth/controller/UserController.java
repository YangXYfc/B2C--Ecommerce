package com.team.ecommerce.auth.controller;

import com.team.ecommerce.auth.dto.ChangePasswordRequest;
import com.team.ecommerce.auth.dto.UpdateProfileRequest;
import com.team.ecommerce.auth.dto.UpdateProfileResponse;
import com.team.ecommerce.auth.service.UserService;
import com.team.ecommerce.common.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户个人中心接口（契约第 1 节）：更新资料、修改密码。
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/profile")
    public Result<UpdateProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest req) {
        return Result.success(userService.updateProfile(req), "更新成功");
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        userService.changePassword(req);
        return Result.success(null, "密码修改成功");
    }
}
