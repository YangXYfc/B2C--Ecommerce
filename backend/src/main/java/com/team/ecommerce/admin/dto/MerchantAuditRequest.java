package com.team.ecommerce.admin.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 审核商家请求（7.3）：approve true-通过 false-驳回，remark 审核备注（可选）。
 */
public record MerchantAuditRequest(
        @NotNull(message = "approve不能为空")
        Boolean approve,
        String remark) {
}
