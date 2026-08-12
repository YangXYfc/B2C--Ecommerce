package com.team.ecommerce.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 待审核商品列表项（8.1，纯数组非分页）。
 */
public record AdminProductPendingVO(
        Long id,
        String name,
        String mainImage,
        BigDecimal price,
        Integer status,
        Long merchantId,
        String shopName,
        Long categoryId,
        LocalDateTime createdAt) {
}
