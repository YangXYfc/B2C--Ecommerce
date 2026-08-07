package com.team.ecommerce.aftersales.refund.dto;

import jakarta.validation.constraints.NotBlank;

public record ReturnLogisticsRequest(@NotBlank String logisticsCompany, @NotBlank String logisticsNo) {
}
