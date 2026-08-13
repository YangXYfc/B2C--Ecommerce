package com.team.ecommerce.aftersales.review.entity;

import java.time.LocalDateTime;

public record ReviewEntity(
        Long id,
        Long orderId,
        Long productId,
        Long userId,
        String content,
        Integer rating,
        String images,
        Integer isAnonymous,
        String merchantReply,
        LocalDateTime merchantReplyTime,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
