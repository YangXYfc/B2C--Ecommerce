package com.team.ecommerce.aftersales.refund.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RefundEntity(
        Long id,
        String refundNo,
        Long orderId,
        Long userId,
        Long merchantId,
        String reason,
        String description,
        BigDecimal amount,
        Integer status,
        LocalDateTime merchantAuditTime,
        String merchantRemark,
        String returnLogisticsCompany,
        String returnLogisticsNo,
        LocalDateTime returnShipTime,
        LocalDateTime merchantConfirmTime,
        LocalDateTime appealTime,
        String appealReason,
        Long adminId,
        LocalDateTime adminHandleTime,
        String adminRemark,
        LocalDateTime completedTime,
        Integer timeoutHours,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
