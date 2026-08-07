package com.team.ecommerce.admin.entity;

import java.time.LocalDateTime;

public record AdminLogEntity(
        Long id,
        Long adminId,
        String action,
        String targetType,
        Long targetId,
        String detail,
        String ipAddress,
        LocalDateTime createdAt) {
}
