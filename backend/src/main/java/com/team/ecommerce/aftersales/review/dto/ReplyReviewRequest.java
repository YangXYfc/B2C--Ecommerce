package com.team.ecommerce.aftersales.review.dto;

import jakarta.validation.constraints.NotBlank;

public record ReplyReviewRequest(@NotBlank String reply) {
}
