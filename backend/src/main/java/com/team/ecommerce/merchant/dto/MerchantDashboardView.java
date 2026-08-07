package com.team.ecommerce.merchant.dto;

import java.math.BigDecimal;

public record MerchantDashboardView(long orderCount, BigDecimal salesAmount, long pendingShipmentCount) {
}
