package com.team.ecommerce.auth.dto;

/**
 * 登录响应（1.2）：JWT 与用户信息。
 */
public record LoginResponse(
        String token,
        UserVO user
) {
}
