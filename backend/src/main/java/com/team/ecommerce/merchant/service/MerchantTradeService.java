package com.team.ecommerce.merchant.service;

import com.team.ecommerce.common.api.PageResult;
import com.team.ecommerce.merchant.dto.MerchantDashboardView;
import com.team.ecommerce.merchant.dto.MerchantOrderQuery;
import com.team.ecommerce.merchant.dto.MerchantOrderView;
import com.team.ecommerce.merchant.dto.ShipOrderRequest;

public interface MerchantTradeService {
    MerchantDashboardView dashboard(Long merchantId);
    PageResult<MerchantOrderView> listOrders(Long merchantId, MerchantOrderQuery query);
    MerchantOrderView orderDetail(Long merchantId, Long orderId);
    MerchantOrderView ship(Long merchantId, Long orderId, ShipOrderRequest request);
}
