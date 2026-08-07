package com.team.ecommerce.admin.dto;

import java.math.BigDecimal;

public record AdminDashboardView(long userCount, long merchantCount, long orderCount, BigDecimal salesAmount) {
}
