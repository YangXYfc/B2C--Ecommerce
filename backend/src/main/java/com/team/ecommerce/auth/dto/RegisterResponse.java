package com.team.ecommerce.auth.dto;

/**
 * 注册响应（1.1）。
 */
public record RegisterResponse(
        Long id,
        String username,
        String nickname,
        String phone,
        String role
) {
}
