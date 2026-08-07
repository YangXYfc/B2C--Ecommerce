package com.team.ecommerce.merchant.dto;

import com.team.ecommerce.trade.order.dto.OrderItemView;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record MerchantOrderView(Long id, String orderNo, Long userId, BigDecimal totalAmount,
        BigDecimal payAmount, Integer status, String addressSnapshot, String logisticsCompany,
        String logisticsNo, LocalDateTime createdAt, List<OrderItemView> items) {
}
