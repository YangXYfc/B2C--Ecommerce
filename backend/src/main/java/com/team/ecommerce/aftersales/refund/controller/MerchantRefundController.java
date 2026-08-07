package com.team.ecommerce.aftersales.refund.controller;

import com.team.ecommerce.aftersales.refund.dto.AuditRefundRequest;
import com.team.ecommerce.aftersales.refund.dto.RefundQuery;
import com.team.ecommerce.aftersales.refund.dto.RefundView;
import com.team.ecommerce.aftersales.refund.service.RefundService;
import com.team.ecommerce.common.api.ApiResponse;
import com.team.ecommerce.common.api.PageResult;
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
@RequestMapping("/api/merchant/refunds")
public class MerchantRefundController {
    private final RefundService refundService;
    public MerchantRefundController(RefundService refundService) { this.refundService = refundService; }

    @GetMapping
    public ApiResponse<PageResult<RefundView>> list(@RequestHeader("X-Merchant-Id") Long merchantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return ApiResponse.success(refundService.listForMerchant(merchantId, new RefundQuery(status, page, size)));
    }

    @PutMapping("/{id}/audit")
    public ApiResponse<RefundView> audit(@RequestHeader("X-Merchant-Id") Long merchantId,
            @PathVariable Long id, @Valid @RequestBody AuditRefundRequest request) {
        return ApiResponse.success(refundService.merchantAudit(merchantId, id, request));
    }

    @PutMapping("/{id}/confirm-return")
    public ApiResponse<RefundView> confirmReturn(@RequestHeader("X-Merchant-Id") Long merchantId,
            @PathVariable Long id) {
        return ApiResponse.success(refundService.confirmReturn(merchantId, id));
    }
}
