package com.team.ecommerce.trade.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderSummaryView(Long id, String orderNo, Long merchantId, BigDecimal totalAmount,
        BigDecimal payAmount, Integer status, LocalDateTime createdAt, List<OrderItemView> items) {
}
