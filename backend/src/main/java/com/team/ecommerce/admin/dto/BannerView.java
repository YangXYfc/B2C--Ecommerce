package com.team.ecommerce.admin.dto;

import java.time.LocalDateTime;

public record BannerView(Long id, String title, String imageUrl, String linkUrl,
        Integer sort, Boolean enabled, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
