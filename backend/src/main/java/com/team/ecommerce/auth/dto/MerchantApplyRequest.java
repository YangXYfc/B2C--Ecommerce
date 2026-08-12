package com.team.ecommerce.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 商家入驻申请请求（1.3）。
 */
public record MerchantApplyRequest(
        @NotBlank(message = "店铺名称不能为空")
        @Size(max = 128, message = "店铺名称长度不能超过128")
        String shopName,

        @NotBlank(message = "联系电话不能为空")
        @Size(max = 20, message = "联系电话长度不能超过20")
        String contactPhone,

        @Size(max = 512, message = "店铺描述长度不能超过512")
        String description,

        @Size(max = 255, message = "店铺Logo地址长度不能超过255")
        String shopLogo
) {
}
