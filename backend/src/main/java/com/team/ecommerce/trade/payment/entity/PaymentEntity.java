package com.team.ecommerce.trade.payment.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentEntity(
        Long id,
        String paymentNo,
        Long orderId,
        Long userId,
        BigDecimal amount,
        String payMethod,
        Integer status,
        LocalDateTime payTime,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
