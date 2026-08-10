package com.team.ecommerce.admin.controller;

import com.team.ecommerce.admin.dto.AdminUserVO;
import com.team.ecommerce.admin.dto.UserStatusRequest;
import com.team.ecommerce.admin.dto.UserStatusVO;
import com.team.ecommerce.admin.service.AdminUserService;
import com.team.ecommerce.common.PageResult;
import com.team.ecommerce.common.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 平台用户管理接口（6.1 / 6.2），由 JwtAuthInterceptor 按前缀限制 ADMIN 角色。
 */
@RestController
@RequestMapping("/api/admin/users")
public class UserAdminController {

    private final AdminUserService adminUserService;

    public UserAdminController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    /** 6.1 用户列表（不返回密码）。 */
    @GetMapping
    public Result<PageResult<AdminUserVO>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return Result.success(adminUserService.list(keyword, role, status, page, size));
    }

    /** 6.2 禁用/启用用户。 */
    @PutMapping("/{id}/status")
    public Result<UserStatusVO> updateStatus(@PathVariable Long id, @Valid @RequestBody UserStatusRequest req) {
        return Result.success(adminUserService.updateStatus(id, req.status()), "操作成功");
    }
}
