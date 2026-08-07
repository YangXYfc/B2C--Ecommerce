package com.team.ecommerce.trade.order.controller;

import com.team.ecommerce.common.api.ApiResponse;
import com.team.ecommerce.common.api.PageResult;
import com.team.ecommerce.trade.order.dto.CancelOrderRequest;
import com.team.ecommerce.trade.order.dto.CreateOrderRequest;
import com.team.ecommerce.trade.order.dto.OrderDetailView;
import com.team.ecommerce.trade.order.dto.OrderQuery;
import com.team.ecommerce.trade.order.dto.OrderSummaryView;
import com.team.ecommerce.trade.order.dto.PaymentView;
import com.team.ecommerce.trade.order.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @PostMapping
    public ApiResponse<OrderDetailView> create(@RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.success(orderService.create(userId, request));
    }

    @GetMapping
    public ApiResponse<PageResult<OrderSummaryView>> list(@RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return ApiResponse.success(orderService.list(userId, new OrderQuery(status, page, size)));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDetailView> detail(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        return ApiResponse.success(orderService.detail(userId, id));
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id,
            @Valid @RequestBody CancelOrderRequest request) {
        orderService.cancel(userId, id, request);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/pay")
    public ApiResponse<PaymentView> pay(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        return ApiResponse.success(orderService.pay(userId, id));
    }

    @PutMapping("/{id}/confirm-receipt")
    public ApiResponse<OrderDetailView> confirmReceipt(@RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        return ApiResponse.success(orderService.confirmReceipt(userId, id));
    }
}
