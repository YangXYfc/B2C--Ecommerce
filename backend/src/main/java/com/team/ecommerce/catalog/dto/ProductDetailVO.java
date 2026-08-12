package com.team.ecommerce.catalog.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品详情（4.2 公开详情）。
 */
public record ProductDetailVO(
        Long id,
        String name,
        String subtitle,
        String mainImage,
        List<String> subImages,
        String description,
        String detailHtml,
        BigDecimal price,
        Integer salesCount,
        Long merchantId,
        String merchantName,
        Long categoryId,
        String categoryName,
        List<SkuVO> skus) {
}
