package com.team.ecommerce.aftersales.refund.dto;

import jakarta.validation.constraints.NotNull;

public record AuditRefundRequest(@NotNull Boolean approved, String remark) {
}
