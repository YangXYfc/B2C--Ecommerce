package com.team.ecommerce.aftersales.refund.dto;

import jakarta.validation.constraints.NotNull;

public record ArbitrateRefundRequest(@NotNull Boolean supportUser, String remark) {
}
