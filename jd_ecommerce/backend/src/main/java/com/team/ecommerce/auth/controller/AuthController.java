package com.team.ecommerce.auth.controller;

import com.team.ecommerce.auth.dto.LoginRequest;
import com.team.ecommerce.auth.dto.LoginResponse;
import com.team.ecommerce.auth.dto.MerchantApplyRequest;
import com.team.ecommerce.auth.dto.MerchantApplyResponse;
import com.team.ecommerce.auth.dto.ProfileVO;
import com.team.ecommerce.auth.dto.RegisterRequest;
import com.team.ecommerce.auth.dto.RegisterResponse;
import com.team.ecommerce.auth.service.AuthService;
import com.team.ecommerce.common.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 账号接口（契约第 1 节）：注册、登录、商家入驻申请、当前用户资料。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Result<RegisterResponse> register(@Valid @RequestBody RegisterRequest req) {
        return Result.success(authService.register(req), "注册成功");
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return Result.success(authService.login(req), "登录成功");
    }

    @PostMapping("/merchant-apply")
    public Result<MerchantApplyResponse> merchantApply(@Valid @RequestBody MerchantApplyRequest req) {
        return Result.success(authService.merchantApply(req), "申请已提交，等待平台审核");
    }

    @GetMapping("/profile")
    public Result<ProfileVO> profile() {
        return Result.success(authService.profile());
    }
}
