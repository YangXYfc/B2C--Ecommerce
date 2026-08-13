package com.team.ecommerce.aftersales.refund.dto;

import jakarta.validation.constraints.NotBlank;

public record AppealRefundRequest(@NotBlank String reason) {
}
