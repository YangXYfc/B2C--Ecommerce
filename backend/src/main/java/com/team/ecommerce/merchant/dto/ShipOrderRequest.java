package com.team.ecommerce.merchant.dto;

import jakarta.validation.constraints.NotBlank;

public record ShipOrderRequest(@NotBlank String logisticsCompany, @NotBlank String logisticsNo) {
}
