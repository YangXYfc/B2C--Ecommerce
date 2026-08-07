package com.team.ecommerce.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BannerRequest(@NotBlank String title, @NotBlank String imageUrl,
        String linkUrl, @NotNull Integer sort, @NotNull Boolean enabled) {
}
