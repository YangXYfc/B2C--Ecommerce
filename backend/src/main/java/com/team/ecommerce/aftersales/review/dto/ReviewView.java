package com.team.ecommerce.aftersales.review.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewView(Long id, Long orderId, Long productId, Long userId, String content,
        Integer rating, List<String> images, Boolean anonymous, String merchantReply,
        LocalDateTime merchantReplyTime, LocalDateTime createdAt) {
}
