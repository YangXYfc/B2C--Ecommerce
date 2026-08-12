package com.team.ecommerce.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Map;

/**
 * SKU 请求（4.6 / 4.7 的 skus[] 元素）。
 */
public record SkuRequest(
        @NotBlank(message = "SKU名称不能为空")
        String skuName,

        @NotNull(message = "SKU售价不能为空")
        @DecimalMin(value = "0.01", message = "SKU售价必须大于0")
        BigDecimal price,

        BigDecimal originalPrice,

        @NotNull(message = "SKU库存不能为空")
        @Min(value = 0, message = "SKU库存不能为负数")
        Integer stock,

        Map<String, String> attributes,

        String skuImage) {
}
