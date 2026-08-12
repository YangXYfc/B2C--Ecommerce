package com.team.ecommerce.auth.dto;

import jakarta.validation.constraints.Size;

/**
 * 更新个人资料请求（1.5），三个字段均可选，至少传一个。
 */
public record UpdateProfileRequest(
        @Size(max = 64, message = "昵称长度不能超过64")
        String nickname,

        @Size(max = 255, message = "头像地址长度不能超过255")
        String avatar,

        @Size(max = 20, message = "手机号长度不能超过20")
        String phone
) {
}
