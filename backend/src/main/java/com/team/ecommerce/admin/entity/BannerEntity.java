package com.team.ecommerce.admin.entity;

import java.time.LocalDateTime;

public record BannerEntity(
        Long id,
        String title,
        String imageUrl,
        String linkUrl,
        Integer sort,
        Integer status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
