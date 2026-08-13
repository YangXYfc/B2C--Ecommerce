package com.team.ecommerce.admin.dto;

import java.time.LocalDateTime;

public record AdminLogView(Long id, Long adminId, String action, String targetType,
        Long targetId, String detail, String ipAddress, LocalDateTime createdAt) {
}
