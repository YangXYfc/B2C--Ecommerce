package com.team.ecommerce.aftersales.review.service;

import com.team.ecommerce.aftersales.review.dto.CreateReviewRequest;
import com.team.ecommerce.aftersales.review.dto.ReplyReviewRequest;
import com.team.ecommerce.aftersales.review.dto.ReviewQuery;
import com.team.ecommerce.aftersales.review.dto.ReviewView;
import com.team.ecommerce.aftersales.review.entity.ReviewEntity;
import com.team.ecommerce.aftersales.review.mapper.ReviewMapper;
import com.team.ecommerce.common.api.PageResult;
import com.team.ecommerce.common.error.BusinessException;
import com.team.ecommerce.common.error.ErrorCode;
import com.team.ecommerce.trade.order.OrderStatus;
import com.team.ecommerce.trade.order.mapper.OrderMapper;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final OrderMapper orderMapper;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ReviewServiceImpl(ReviewMapper reviewMapper, OrderMapper orderMapper) {
        this.reviewMapper = reviewMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public ReviewView create(Long userId, CreateReviewRequest request) {
        // Verify order exists, belongs to user, and is in RECEIVED status
        var order = orderMapper.selectById(request.orderId());
        if (order == null || !order.userId().equals(userId)) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "订单不存在");
        }
        if (order.status() != OrderStatus.RECEIVED.getCode()
                && order.status() != OrderStatus.REVIEWED.getCode()) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "只能评价已收货的订单");
        }

        var imagesJson = toJson(request.images());
        var entity = new ReviewEntity(null, request.orderId(), request.productId(), userId,
                request.content(), request.rating(), imagesJson,
                request.anonymous() != null && request.anonymous() ? 1 : 0,
                null, null, null, null);
        reviewMapper.insert(entity);
        var inserted = new ReviewEntity(reviewMapper.lastInsertId(), entity.orderId(),
                entity.productId(), entity.userId(), entity.content(), entity.rating(),
                entity.images(), entity.isAnonymous(), entity.merchantReply(),
                entity.merchantReplyTime(), entity.createdAt(), entity.updatedAt());

        // Update order status to REVIEWED
        if (order.status() == OrderStatus.RECEIVED.getCode()) {
            var updated = new com.team.ecommerce.trade.order.entity.OrderEntity(
                    order.id(), order.orderNo(), order.userId(), order.merchantId(),
                    order.totalAmount(), order.payAmount(), OrderStatus.REVIEWED.getCode(),
                    order.addressSnapshot(), order.remark(), order.logisticsCompany(),
                    order.logisticsNo(), order.shipTime(), order.receiveTime(),
                    order.payTime(), order.cancelTime(), order.cancelReason(),
                    order.createdAt(), order.updatedAt());
            orderMapper.updateById(updated);
        }

        return toView(inserted);
    }

    @Override
    public PageResult<ReviewView> listByProduct(Long productId, ReviewQuery query) {
        int offset = (query.page() - 1) * query.size();
        var reviews = reviewMapper.selectByProductId(productId, offset, query.size());
        var views = reviews.stream().map(ReviewServiceImpl::toView).toList();
        long total = reviewMapper.countByProductId(productId);
        return new PageResult<>(views, total, query.page(), query.size());
    }

    @Override
    public PageResult<ReviewView> listForMerchant(Long merchantId, ReviewQuery query) {
        int offset = (query.page() - 1) * query.size();
        var reviews = reviewMapper.selectByMerchantId(merchantId, offset, query.size());
        var views = reviews.stream().map(ReviewServiceImpl::toView).toList();
        long total = reviewMapper.countByMerchantId(merchantId);
        return new PageResult<>(views, total, query.page(), query.size());
    }

    @Override
    @Transactional
    public ReviewView reply(Long merchantId, Long reviewId, ReplyReviewRequest request) {
        var review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "评价不存在");
        }
        // Verify merchant owns the product
        var order = orderMapper.selectById(review.orderId());
        if (order == null || !order.merchantId().equals(merchantId)) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "无权回复该评价");
        }
        var updated = new ReviewEntity(review.id(), review.orderId(), review.productId(),
                review.userId(), review.content(), review.rating(), review.images(),
                review.isAnonymous(), request.reply(), LocalDateTime.now(),
                review.createdAt(), review.updatedAt());
        reviewMapper.updateById(updated);
        return toView(updated);
    }

    private static ReviewView toView(ReviewEntity e) {
        return new ReviewView(e.id(), e.orderId(), e.productId(), e.userId(),
                e.content(), e.rating(), fromJson(e.images()),
                e.isAnonymous() == 1, e.merchantReply(),
                e.merchantReplyTime(), e.createdAt());
    }

    private static String toJson(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static List<String> fromJson(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, List.class);
        } catch (JsonProcessingException ex) {
            return List.of();
        }
    }
}
