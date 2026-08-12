package com.team.ecommerce.catalog.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商家商品详情（4.5，供编辑页回填，含审核信息）。
 */
public record MerchantProductDetailVO(
        Long id,
        String name,
        String subtitle,
        String mainImage,
        List<String> subImages,
        String description,
        String detailHtml,
        BigDecimal price,
        Integer status,
        String auditRemark,
        Integer salesCount,
        Long categoryId,
        List<SkuVO> skus) {
}
