package com.team.ecommerce.admin.dto;

import java.time.LocalDateTime;

/**
 * 平台用户列表项（6.1，不含密码）。
 */
public record AdminUserVO(
        Long id,
        String username,
        String nickname,
        String phone,
        String email,
        String role,
        Integer status,
        LocalDateTime createdAt) {
}
