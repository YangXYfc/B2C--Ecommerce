package com.team.ecommerce.aftersales.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.team.ecommerce.aftersales.review.dto.CreateReviewRequest;
import com.team.ecommerce.aftersales.review.dto.ReplyReviewRequest;
import com.team.ecommerce.aftersales.review.dto.ReviewQuery;
import com.team.ecommerce.aftersales.review.service.ReviewService;
import com.team.ecommerce.common.error.BusinessException;
import com.team.ecommerce.trade.order.OrderStatus;
import com.team.ecommerce.trade.order.mapper.OrderMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ReviewServiceIntegrationTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private OrderMapper orderMapper;

    @Test
    void createReviewOnReceivedOrderMarksOrderReviewed() {
        // Order 1 belongs to user 4, status RECEIVED(3)
        var review = reviewService.create(4L, new CreateReviewRequest(1L, 1L,
                "手机很好用", 5, List.of("http://img/review.jpg"), false));
        assertNotNull(review.id());
        assertEquals(5, review.rating());
        // Order status should become REVIEWED(4)
        assertEquals(OrderStatus.REVIEWED.getCode(), orderMapper.selectById(1L).status());
    }

    @Test
    void createReviewOnOrderOfAnotherUserFails() {
        // Order 1 belongs to user 4, but user 5 tries to review
        assertThrows(BusinessException.class,
                () -> reviewService.create(5L, new CreateReviewRequest(1L, 1L, "x", 5, null, false)));
    }

    @Test
    void createReviewOnNonReceivedOrderFails() {
        // Order 4 belongs to user 6, status PENDING_PAYMENT(0)
        assertThrows(BusinessException.class,
                () -> reviewService.create(6L, new CreateReviewRequest(4L, 2L, "x", 5, null, false)));
    }

    @Test
    void listReviewsByProduct() {
        // Review 1 is for product 3
        var page = reviewService.listByProduct(3L, new ReviewQuery(null, 1, 10));
        assertEquals(1, page.records().size());
    }

    @Test
    void listReviewsForMerchant() {
        // Review 1 is for product 3 (merchant 1)
        var page = reviewService.listForMerchant(1L, new ReviewQuery(null, 1, 10));
        assertEquals(1, page.records().size());
    }

    @Test
    void merchantRepliesToOwnProductReview() {
        var review = reviewService.reply(1L, 1L, new ReplyReviewRequest("感谢支持！"));
        assertEquals("感谢支持！", review.merchantReply());
        assertNotNull(review.merchantReplyTime());
    }

    @Test
    void otherMerchantCannotReply() {
        // Review 1's order is from merchant 1; merchant 2 has no right
        assertThrows(BusinessException.class,
                () -> reviewService.reply(2L, 1L, new ReplyReviewRequest("x")));
    }
}
