package com.team.ecommerce.aftersales.review.service;

import com.team.ecommerce.aftersales.review.dto.CreateReviewRequest;
import com.team.ecommerce.aftersales.review.dto.ReplyReviewRequest;
import com.team.ecommerce.aftersales.review.dto.ReviewQuery;
import com.team.ecommerce.aftersales.review.dto.ReviewView;
import com.team.ecommerce.common.api.PageResult;
import com.team.ecommerce.common.error.FeatureNotImplementedException;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceSkeleton implements ReviewService {
    public ReviewView create(Long userId, CreateReviewRequest request) { throw pending("review.create"); }
    public PageResult<ReviewView> listByProduct(Long productId, ReviewQuery query) { throw pending("review.listByProduct"); }
    public PageResult<ReviewView> listForMerchant(Long merchantId, ReviewQuery query) { throw pending("review.listForMerchant"); }
    public ReviewView reply(Long merchantId, Long reviewId, ReplyReviewRequest request) { throw pending("review.reply"); }
    private FeatureNotImplementedException pending(String operation) { return new FeatureNotImplementedException(operation); }
}
