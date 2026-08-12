package com.team.ecommerce.auth.dto;

/**
 * 个人资料中嵌套的商家信息（1.4），仅角色为 MERCHANT 时返回。
 */
public record MerchantVO(
        Long id,
        String shopName,
        String shopLogo,
        Integer auditStatus
) {
}
