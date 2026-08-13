package com.team.ecommerce.aftersales.refund.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateRefundRequest(@NotNull Long orderId, @NotBlank String reason,
        String description, @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal amount) {
}
