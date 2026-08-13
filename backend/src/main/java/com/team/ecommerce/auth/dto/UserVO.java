package com.team.ecommerce.auth.dto;

/**
 * 登录响应用户信息（1.2）。
 */
public record UserVO(
        Long id,
        String username,
        String nickname,
        String avatar,
        String role,
        Integer status
) {
}
