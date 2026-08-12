package com.team.ecommerce.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 禁用/启用用户请求（6.2）：status 0-禁用 1-正常。
 */
public record UserStatusRequest(
        @NotNull(message = "状态不能为空")
        @Min(value = 0, message = "状态非法")
        @Max(value = 1, message = "状态非法")
        Integer status) {
}
