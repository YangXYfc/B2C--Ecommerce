package com.team.ecommerce.trade.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentView(Long id, String paymentNo, Long orderId, BigDecimal amount,
        String payMethod, Integer status, LocalDateTime payTime) {
}
