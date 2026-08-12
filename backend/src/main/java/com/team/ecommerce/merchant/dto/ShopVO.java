package com.team.ecommerce.merchant.dto;

/**
 * 店铺资料视图（5.1 / 5.2）。
 */
public record ShopVO(
        String shopName,
        String shopLogo,
        String description,
        String contactPhone,
        Integer auditStatus) {
}
