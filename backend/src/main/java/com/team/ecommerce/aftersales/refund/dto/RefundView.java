package com.team.ecommerce.aftersales.refund.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RefundView(Long id, String refundNo, Long orderId, Long userId, Long merchantId,
        String reason, String description, BigDecimal amount, Integer status,
        String returnLogisticsCompany, String returnLogisticsNo, String appealReason,
        String merchantRemark, String adminRemark, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
