package com.team.ecommerce.catalog.dto;

import java.math.BigDecimal;

/**
 * 商品列表项（4.1 公开列表）。
 */
public record ProductListVO(
        Long id,
        String name,
        String subtitle,
        String mainImage,
        BigDecimal price,
        Integer salesCount) {
}
