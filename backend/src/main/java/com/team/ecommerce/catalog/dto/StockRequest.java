package com.team.ecommerce.catalog.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 修改 SKU 库存请求（4.9）。
 */
public record StockRequest(
        @NotNull(message = "库存不能为空")
        @Min(value = 0, message = "库存不能为负数")
        Integer stock) {
}
