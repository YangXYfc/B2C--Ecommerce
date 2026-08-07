package com.team.ecommerce.trade.order.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderEntity(
        Long id,
        String orderNo,
        Long userId,
        Long merchantId,
        BigDecimal totalAmount,
        BigDecimal payAmount,
        Integer status,
        String addressSnapshot,
        String remark,
        String logisticsCompany,
        String logisticsNo,
        LocalDateTime shipTime,
        LocalDateTime receiveTime,
        LocalDateTime payTime,
        LocalDateTime cancelTime,
        String cancelReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
