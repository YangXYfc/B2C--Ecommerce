package com.team.ecommerce.auth.dto;

/**
 * 收货地址视图对象（2.1~2.5 响应）。
 */
public record AddressVO(
        Long id,
        String name,
        String phone,
        String province,
        String city,
        String district,
        String detail,
        Integer isDefault
) {
}
