package com.team.ecommerce.aftersales.review.controller;

import com.team.ecommerce.aftersales.review.dto.ReplyReviewRequest;
import com.team.ecommerce.aftersales.review.dto.ReviewQuery;
import com.team.ecommerce.aftersales.review.dto.ReviewView;
import com.team.ecommerce.aftersales.review.service.ReviewService;
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
@RequestMapping("/api/merchant/reviews")
public class MerchantReviewController {
    private final ReviewService reviewService;
    public MerchantReviewController(ReviewService reviewService) { this.reviewService = reviewService; }

    @GetMapping
    public ApiResponse<PageResult<ReviewView>> list(@RequestHeader("X-Merchant-Id") Long merchantId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return ApiResponse.success(reviewService.listForMerchant(merchantId, new ReviewQuery(rating, page, size)));
    }

    @PutMapping("/{id}/reply")
    public ApiResponse<ReviewView> reply(@RequestHeader("X-Merchant-Id") Long merchantId,
            @PathVariable Long id, @Valid @RequestBody ReplyReviewRequest request) {
        return ApiResponse.success(reviewService.reply(merchantId, id, request));
    }
}
