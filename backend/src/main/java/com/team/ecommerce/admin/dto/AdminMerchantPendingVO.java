package com.team.ecommerce.admin.dto;

import java.time.LocalDateTime;

/**
 * 待审核商家列表项（7.1，纯数组非分页）。
 */
public record AdminMerchantPendingVO(
        Long id,
        String shopName,
        String description,
        String contactPhone,
        Applicant applicant,
        LocalDateTime createdAt) {

    /** 申请人（商家账号）：仅需用户名/昵称。 */
    public record Applicant(String username, String nickname) {
    }
}
