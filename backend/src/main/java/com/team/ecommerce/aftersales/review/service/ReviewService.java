package com.team.ecommerce.aftersales.review.service;

import com.team.ecommerce.aftersales.review.dto.CreateReviewRequest;
import com.team.ecommerce.aftersales.review.dto.ReplyReviewRequest;
import com.team.ecommerce.aftersales.review.dto.ReviewQuery;
import com.team.ecommerce.aftersales.review.dto.ReviewView;
import com.team.ecommerce.common.api.PageResult;

public interface ReviewService {
    ReviewView create(Long userId, CreateReviewRequest request);
    PageResult<ReviewView> listByProduct(Long productId, ReviewQuery query);
    PageResult<ReviewView> listForMerchant(Long merchantId, ReviewQuery query);
    ReviewView reply(Long merchantId, Long reviewId, ReplyReviewRequest request);
}
