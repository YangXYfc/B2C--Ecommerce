package com.team.ecommerce.admin.service;

import java.math.BigDecimal;
import org.apache.ibatis.annotations.Mapper;

/** Simple COUNT / SUM queries for the admin dashboard. */
@Mapper
interface AdminDashboardMapper {
    long countUsers();
    long countMerchants();
    long countOrders();
    BigDecimal sumSales();
}
