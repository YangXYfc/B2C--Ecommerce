package com.team.ecommerce.merchant.service;

import com.team.ecommerce.common.api.PageResult;
import com.team.ecommerce.common.error.FeatureNotImplementedException;
import com.team.ecommerce.merchant.dto.MerchantDashboardView;
import com.team.ecommerce.merchant.dto.MerchantOrderQuery;
import com.team.ecommerce.merchant.dto.MerchantOrderView;
import com.team.ecommerce.merchant.dto.ShipOrderRequest;
import org.springframework.stereotype.Service;

@Service
public class MerchantTradeServiceSkeleton implements MerchantTradeService {
    public MerchantDashboardView dashboard(Long merchantId) { throw pending("merchant.dashboard"); }
    public PageResult<MerchantOrderView> listOrders(Long merchantId, MerchantOrderQuery query) { throw pending("merchant.orders.list"); }
    public MerchantOrderView orderDetail(Long merchantId, Long orderId) { throw pending("merchant.orders.detail"); }
    public MerchantOrderView ship(Long merchantId, Long orderId, ShipOrderRequest request) { throw pending("merchant.orders.ship"); }
    private FeatureNotImplementedException pending(String operation) { return new FeatureNotImplementedException(operation); }
}
