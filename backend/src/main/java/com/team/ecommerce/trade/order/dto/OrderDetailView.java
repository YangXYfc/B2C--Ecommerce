package com.team.ecommerce.trade.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailView(Long id, String orderNo, Long merchantId, BigDecimal totalAmount,
        BigDecimal payAmount, Integer status, String addressSnapshot, String remark,
        String logisticsCompany, String logisticsNo, LocalDateTime createdAt, List<OrderItemView> items) {
}
