package com.team.ecommerce.aftersales.review.controller;

import com.team.ecommerce.aftersales.review.dto.CreateReviewRequest;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class ReviewController {
    private final ReviewService reviewService;
    public ReviewController(ReviewService reviewService) { this.reviewService = reviewService; }

    @PostMapping("/api/reviews")
    public ApiResponse<ReviewView> create(@RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateReviewRequest request) {
        return ApiResponse.success(reviewService.create(userId, request));
    }

    @GetMapping("/api/products/{id}/reviews")
    public ApiResponse<PageResult<ReviewView>> listByProduct(@PathVariable Long id,
            @RequestParam(required = false) Integer rating,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return ApiResponse.success(reviewService.listByProduct(id, new ReviewQuery(rating, page, size)));
    }
}
