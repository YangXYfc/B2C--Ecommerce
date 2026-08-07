package com.team.ecommerce.aftersales.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateReviewRequest(@NotNull Long orderId, @NotNull Long productId,
        String content, @NotNull @Min(1) @Max(5) Integer rating, List<String> images, Boolean anonymous) {
}
