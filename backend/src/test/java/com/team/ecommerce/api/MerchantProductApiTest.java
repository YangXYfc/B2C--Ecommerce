package com.team.ecommerce.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 商家商品/SKU 接口集成测试（契约 4.4-4.9）。
 * 种子数据：merchant1 拥有商品 1,2,3,4,7(上架)、9(拒绝) 共 6 个；merchant2 拥有 5,6,8。
 */
class MerchantProductApiTest extends AbstractApiTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 4.4 本店商品列表
    @Test
    void merchantList_returnsOwnProducts() throws Exception {
        expectOk(doGet("/api/merchant/products", tokenOf(MERCHANT1)))
                .andExpect(jsonPath("$.data.total").value(6));
    }

    @Test
    void merchantList_statusFilter() throws Exception {
        expectOk(doGet("/api/merchant/products?status=1", tokenOf(MERCHANT1)))
                .andExpect(jsonPath("$.data.total").value(5));
        expectOk(doGet("/api/merchant/products?status=3", tokenOf(MERCHANT1)))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    // 4.5 本店商品详情
    @Test
    void merchantDetail_own() throws Exception {
        expectOk(doGet("/api/merchant/products/1", tokenOf(MERCHANT1)))
                .andExpect(jsonPath("$.data.skus", hasSize(4)))
                .andExpect(jsonPath("$.data.status").value(1));
    }

    @Test
    void merchantDetail_otherMerchant_403() throws Exception {
        expectError(doGet("/api/merchant/products/5", tokenOf(MERCHANT1)), 403);
    }

    @Test
    void merchantDetail_notFound_404() throws Exception {
        expectError(doGet("/api/merchant/products/99999", tokenOf(MERCHANT1)), 404);
    }

    // 4.6 发布商品
    @Test
    void create_success_returnsProductId_thenPending() throws Exception {
        MvcResult create = expectOk(doPost("/api/merchant/products", tokenOf(MERCHANT1),
                productBody(111L, unique("IT商品")))).andReturn();
        int productId = readJson(create, "$.data.productId");
        assertTrue(productId > 0);
        expectOk(doGet("/api/merchant/products/" + productId, tokenOf(MERCHANT1)))
                .andExpect(jsonPath("$.data.status").value(0));
    }

    @Test
    void create_missingSkus_400() throws Exception {
        expectError(doPost("/api/merchant/products", tokenOf(MERCHANT1),
                productBody(111L, unique("IT商品"), List.of())), 400);
    }

    @Test
    void create_badCategory_400() throws Exception {
        expectError(doPost("/api/merchant/products", tokenOf(MERCHANT1),
                productBody(99999L, unique("IT商品"))), 400);
    }

    @Test
    void create_zeroPriceSku_400() throws Exception {
        expectError(doPost("/api/merchant/products", tokenOf(MERCHANT1),
                productBody(111L, unique("IT商品"), List.of(sku("SKU-A", BigDecimal.ZERO, 10)))), 400);
    }

    @Test
    void create_asUser_403() throws Exception {
        expectError(doPost("/api/merchant/products", tokenOf(USER1),
                productBody(111L, unique("IT商品"))), 403);
    }

    @Test
    void create_asPendingMerchant_403() throws Exception {
        // merchant3 审核未通过（audit_status=0），商品接口一律 403
        expectError(doPost("/api/merchant/products", tokenOf(PENDING_MERCHANT),
                productBody(111L, unique("IT商品"))), 403);
    }

    // 4.7 编辑商品
    @Test
    void update_success_returnsPending() throws Exception {
        // 无订单历史的商品（商品7）：旧 SKU 全部删除、无停用，编辑后回待审核，公开详情不可见
        MvcResult upd = expectOk(doPut("/api/merchant/products/7", tokenOf(MERCHANT1),
                productBody(111L, unique("IT商品")))).andReturn();
        assertEquals(0, (int) readJson(upd, "$.data.status"));
        expectError(doGet("/api/products/7", null), 404);
    }

    @Test
    void update_productWithOrderHistory_200_retiresSoldSku() throws Exception {
        // 商品1（merchant1）的 SKU 1 被 order_item#1 引用：旧实现「删光重建」会因外键 RESTRICT 报 500。
        // 修复后：未卖过的 SKU 2-4 删除、卖过的 SKU 1 保留但停用、新清单 SKU 全部新增。
        expectOk(doPut("/api/merchant/products/1", tokenOf(MERCHANT1),
                productBody(111L, unique("IT商品"))))
                .andExpect(jsonPath("$.data.status").value(0));

        // 商家详情只显示启用 SKU（status=1）→ 恰好 1 条新 SKU，旧规格停用后不可见
        expectOk(doGet("/api/merchant/products/1", tokenOf(MERCHANT1)))
                .andExpect(jsonPath("$.data.skus", hasSize(1)));

        // 被引用的旧 SKU 1 仍物理存在、已停用（order_item 快照继续有效，不再触发 500）
        Integer kept = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM product_sku WHERE id = 1 AND status = 0", Integer.class);
        assertEquals(1, kept);

        // 编辑后回待审核，公开详情 404
        expectError(doGet("/api/products/1", null), 404);
    }

    @Test
    void update_otherMerchant_403() throws Exception {
        expectError(doPut("/api/merchant/products/5", tokenOf(MERCHANT1),
                productBody(111L, unique("IT商品"))), 403);
    }

    @Test
    void update_notFound_404() throws Exception {
        expectError(doPut("/api/merchant/products/99999", tokenOf(MERCHANT1),
                productBody(111L, unique("IT商品"))), 404);
    }

    // 4.8 下架
    @Test
    void offShelf_success_status2_andPublicHidden() throws Exception {
        MvcResult off = expectOk(doPut("/api/merchant/products/1/off-shelf", tokenOf(MERCHANT1), null)).andReturn();
        assertEquals(2, (int) readJson(off, "$.data.status"));
        expectError(doGet("/api/products/1", null), 404);
    }

    // 4.9 改库存
    @Test
    void updateStock_success() throws Exception {
        expectOk(doPut("/api/merchant/skus/1/stock", tokenOf(MERCHANT1), body("stock", 600)))
                .andExpect(jsonPath("$.data.stock").value(600));

        // 公开详情里的 SKU 1 库存应同步
        MvcResult detail = expectOk(doGet("/api/products/1", null)).andReturn();
        List<?> skus = readJson(detail, "$.data.skus");
        Map<?, ?> sku1 = (Map<?, ?>) skus.stream()
                .filter(s -> ((Number) ((Map<?, ?>) s).get("id")).longValue() == 1L)
                .findFirst().orElseThrow();
        assertEquals(600, ((Number) sku1.get("stock")).intValue());
    }

    @Test
    void updateStock_negative_400() throws Exception {
        expectError(doPut("/api/merchant/skus/1/stock", tokenOf(MERCHANT1), body("stock", -1)), 400);
    }

    @Test
    void updateStock_otherMerchantSku_403() throws Exception {
        // SKU 11 属于 merchant2 的商品 5
        expectError(doPut("/api/merchant/skus/11/stock", tokenOf(MERCHANT1), body("stock", 100)), 403);
    }

    @Test
    void updateStock_notFound_404() throws Exception {
        expectError(doPut("/api/merchant/skus/99999/stock", tokenOf(MERCHANT1), body("stock", 100)), 404);
    }

    @Test
    void updateStock_asPendingMerchant_403() throws Exception {
        expectError(doPut("/api/merchant/skus/1/stock", tokenOf(PENDING_MERCHANT), body("stock", 100)), 403);
    }
}
