package com.team.ecommerce.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 新增/编辑收货地址请求（2.2 / 2.3）。
 */
public record AddressRequest(
        @NotBlank(message = "收货人姓名不能为空")
        @Size(max = 64, message = "收货人姓名长度不能超过64")
        String name,

        @NotBlank(message = "收货人手机号不能为空")
        @Size(max = 20, message = "手机号长度不能超过20")
        String phone,

        @NotBlank(message = "省份不能为空")
        @Size(max = 64, message = "省份长度不能超过64")
        String province,

        @NotBlank(message = "城市不能为空")
        @Size(max = 64, message = "城市长度不能超过64")
        String city,

        @Size(max = 64, message = "区/县长度不能超过64")
        String district,

        @NotBlank(message = "详细地址不能为空")
        @Size(max = 255, message = "详细地址长度不能超过255")
        String detail,

        Integer isDefault
) {
}
