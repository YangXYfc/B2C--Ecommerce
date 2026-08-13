package com.team.ecommerce.trade.cart.entity;

import java.time.LocalDateTime;

public record CartEntity(
        Long id,
        Long userId,
        Long productSkuId,
        Integer quantity,
        Integer selected,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
