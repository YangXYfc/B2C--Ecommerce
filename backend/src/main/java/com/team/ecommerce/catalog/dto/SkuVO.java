package com.team.ecommerce.catalog.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * SKU 视图（4.2 / 4.5）。
 * attributes 为规格属性，如 {"颜色":"钛空灰","版本":"12GB+256GB"}。
 */
public record SkuVO(
        Long id,
        String skuName,
        BigDecimal price,
        BigDecimal originalPrice,
        Integer stock,
        Map<String, String> attributes,
        String skuImage) {
}
