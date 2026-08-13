package com.team.ecommerce.trade.order.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderItemEntity(
        Long id,
        Long orderId,
        Long productSkuId,
        String productName,
        String skuName,
        String productImage,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal,
        LocalDateTime createdAt) {
}
