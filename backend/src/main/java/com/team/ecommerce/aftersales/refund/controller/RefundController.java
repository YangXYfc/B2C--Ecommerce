package com.team.ecommerce.aftersales.refund.controller;

import com.team.ecommerce.aftersales.refund.dto.AppealRefundRequest;
import com.team.ecommerce.aftersales.refund.dto.CreateRefundRequest;
import com.team.ecommerce.aftersales.refund.dto.RefundQuery;
import com.team.ecommerce.aftersales.refund.dto.RefundView;
import com.team.ecommerce.aftersales.refund.dto.ReturnLogisticsRequest;
import com.team.ecommerce.aftersales.refund.service.RefundService;
import com.team.ecommerce.common.api.ApiResponse;
import com.team.ecommerce.common.api.PageResult;
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
@RequestMapping("/api/refunds")
public class RefundController {
    private final RefundService refundService;
    public RefundController(RefundService refundService) { this.refundService = refundService; }

    @PostMapping
    public ApiResponse<RefundView> create(@RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateRefundRequest request) {
        return ApiResponse.success(refundService.create(userId, request));
    }

    @GetMapping
    public ApiResponse<PageResult<RefundView>> list(@RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return ApiResponse.success(refundService.listForUser(userId, new RefundQuery(status, page, size)));
    }

    @GetMapping("/{id}")
    public ApiResponse<RefundView> detail(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestHeader(value = "X-Merchant-Id", required = false) Long merchantId,
            @RequestHeader(value = "X-Admin-Id", required = false) Long adminId,
            @PathVariable Long id) {
        return ApiResponse.success(refundService.detail(userId, merchantId, adminId, id));
    }

    @PutMapping("/{id}/return-logistics")
    public ApiResponse<RefundView> returnLogistics(@RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id, @Valid @RequestBody ReturnLogisticsRequest request) {
        return ApiResponse.success(refundService.submitReturnLogistics(userId, id, request));
    }

    @PutMapping("/{id}/appeal")
    public ApiResponse<RefundView> appeal(@RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id, @Valid @RequestBody AppealRefundRequest request) {
        return ApiResponse.success(refundService.appeal(userId, id, request));
    }
}
