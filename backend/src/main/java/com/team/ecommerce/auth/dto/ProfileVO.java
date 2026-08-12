package com.team.ecommerce.auth.dto;

/**
 * 个人资料响应（1.4）。{@code merchant} 仅在角色为 MERCHANT 时非空。
 */
public record ProfileVO(
        Long id,
        String username,
        String nickname,
        String phone,
        String email,
        String avatar,
        Integer gender,
        String role,
        Integer status,
        MerchantVO merchant
) {
}
