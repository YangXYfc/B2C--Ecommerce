package com.team.ecommerce.admin.dto;

import com.team.ecommerce.catalog.dto.SkuVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 待审核商品详情（8.2），含 SKU 列表。
 */
public record AdminProductDetailVO(
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
        Long merchantId,
        String shopName,
        Long categoryId,
        List<SkuVO> skus) {
}
