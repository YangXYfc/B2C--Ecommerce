package com.team.ecommerce.auth.dto;

/**
 * 商家入驻申请响应（1.3）。
 */
public record MerchantApplyResponse(
        Long merchantId,
        Integer auditStatus
) {
}
