package com.team.ecommerce.merchant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 更新店铺资料请求（5.2），字段校验与商家入驻申请（1.3）一致。
 */
public record ShopRequest(
        @NotBlank(message = "店铺名称不能为空")
        @Size(max = 128, message = "店铺名称长度不能超过128")
        String shopName,

        @Size(max = 255, message = "店铺Logo地址长度不能超过255")
        String shopLogo,

        @Size(max = 512, message = "店铺描述长度不能超过512")
        String description,

        @NotBlank(message = "联系电话不能为空")
        @Size(max = 20, message = "联系电话长度不能超过20")
        String contactPhone) {
}
