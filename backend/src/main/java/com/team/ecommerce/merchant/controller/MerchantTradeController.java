package com.team.ecommerce.merchant.controller;

import com.team.ecommerce.common.api.ApiResponse;
import com.team.ecommerce.common.api.PageResult;
import com.team.ecommerce.merchant.dto.MerchantDashboardView;
import com.team.ecommerce.merchant.dto.MerchantOrderQuery;
import com.team.ecommerce.merchant.dto.MerchantOrderView;
import com.team.ecommerce.merchant.dto.ShipOrderRequest;
import com.team.ecommerce.merchant.service.MerchantTradeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/merchant")
public class MerchantTradeController {
    private final MerchantTradeService merchantTradeService;
    public MerchantTradeController(MerchantTradeService merchantTradeService) { this.merchantTradeService = merchantTradeService; }

    @GetMapping("/dashboard")
    public ApiResponse<MerchantDashboardView> dashboard(@RequestHeader("X-Merchant-Id") Long merchantId) {
        return ApiResponse.success(merchantTradeService.dashboard(merchantId));
    }

    @GetMapping("/orders")
    public ApiResponse<PageResult<MerchantOrderView>> listOrders(@RequestHeader("X-Merchant-Id") Long merchantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return ApiResponse.success(merchantTradeService.listOrders(merchantId, new MerchantOrderQuery(status, page, size)));
    }

    @GetMapping("/orders/{id}")
    public ApiResponse<MerchantOrderView> orderDetail(@RequestHeader("X-Merchant-Id") Long merchantId,
            @PathVariable Long id) {
        return ApiResponse.success(merchantTradeService.orderDetail(merchantId, id));
    }

    @PutMapping("/orders/{id}/ship")
    public ApiResponse<MerchantOrderView> ship(@RequestHeader("X-Merchant-Id") Long merchantId,
            @PathVariable Long id, @Valid @RequestBody ShipOrderRequest request) {
        return ApiResponse.success(merchantTradeService.ship(merchantId, id, request));
    }
}
