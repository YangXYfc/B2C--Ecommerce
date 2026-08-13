package com.team.ecommerce.catalog.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商家商品列表项（4.4）。
 */
public record MerchantProductListVO(
        Long id,
        String name,
        String mainImage,
        BigDecimal price,
        Integer status,
        String auditRemark,
        Integer salesCount,
        LocalDateTime createdAt) {
}
