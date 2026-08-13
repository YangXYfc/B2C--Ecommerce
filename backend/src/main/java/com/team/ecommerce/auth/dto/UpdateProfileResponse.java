package com.team.ecommerce.auth.dto;

/**
 * 更新个人资料响应（1.5）。
 */
public record UpdateProfileResponse(
        Long id,
        String username,
        String nickname,
        String avatar,
        String phone
) {
}
