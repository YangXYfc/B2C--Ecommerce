package com.team.ecommerce.api;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 平台管理接口集成测试（契约第 6-8 节：用户管理 / 商家审核 / 商品审核）。
 * 种子数据：用户 7、待审商家 merchant3(3)、待审商品 8。
 */
class AdminApiTest extends AbstractApiTest {

    // 6.1 用户列表
    @Test
    void listUsers_returnsAll_noPassword() throws Exception {
        expectOk(doGet("/api/admin/users", tokenOf(ADMIN)))
                .andExpect(jsonPath("$.data.total").value(7))
                .andExpect(jsonPath("$.data.list[0].password").doesNotExist());
    }

    @Test
    void listUsers_roleFilter_USER() throws Exception {
        expectOk(doGet("/api/admin/users?role=USER", tokenOf(ADMIN)))
                .andExpect(jsonPath("$.data.total").value(3));
    }

    @Test
    void listUsers_keywordFilter() throws Exception {
        expectOk(doGet("/api/admin/users?keyword=merchant", tokenOf(ADMIN)))
                .andExpect(jsonPath("$.data.total").value(3));
    }

    // 6.2 用户状态
    @Test
    void updateUserStatus_disable_thenLoginForbidden() throws Exception {
        expectOk(doPut("/api/admin/users/5/status", tokenOf(ADMIN), body("status", 0)))
                .andExpect(jsonPath("$.data.status").value(0));
        expectError(doPost("/api/auth/login", null, Map.of("username", "user2", "password", "123456")), 403);
    }

    @Test
    void updateUserStatus_disableSelf_400() throws Exception {
        expectError(doPut("/api/admin/users/1/status", tokenOf(ADMIN), body("status", 0)), 400);
    }

    @Test
    void updateUserStatus_invalidStatus_400() throws Exception {
        expectError(doPut("/api/admin/users/5/status", tokenOf(ADMIN), body("status", 2)), 400);
    }

    @Test
    void updateUserStatus_notFound_404() throws Exception {
        expectError(doPut("/api/admin/users/99999/status", tokenOf(ADMIN), body("status", 0)), 404);
    }

    @Test
    void updateUserStatus_nonAdmin_403() throws Exception {
        expectError(doPut("/api/admin/users/5/status", tokenOf(MERCHANT1), body("status", 0)), 403);
    }

    // 7.1 待审商家列表
    @Test
    void listPendingMerchants_containsSeed() throws Exception {
        expectOk(doGet("/api/admin/merchants/pending", tokenOf(ADMIN)))
                .andExpect(jsonPath("$.data[?(@.id == 3)]").isNotEmpty());
    }

    // 7.2 商家申请详情
    @Test
    void merchantDetail_seedPending() throws Exception {
        expectOk(doGet("/api/admin/merchants/3", tokenOf(ADMIN)))
                .andExpect(jsonPath("$.data.applicant.username").value("merchant3"))
                .andExpect(jsonPath("$.data.auditStatus").value(0));
    }

    @Test
    void merchantDetail_notFound_404() throws Exception {
        expectError(doGet("/api/admin/merchants/99999", tokenOf(ADMIN)), 404);
    }

    // 7.3 商家审核
    @Test
    void auditMerchant_approve_seedPending() throws Exception {
        expectOk(doPut("/api/admin/merchants/3/audit", tokenOf(ADMIN), body("approve", true)))
                .andExpect(jsonPath("$.data.auditStatus").value(1));
        // 审核通过后，merchant3 即可访问商家商品接口（之前 403）
        expectOk(doGet("/api/merchant/products", tokenOf(PENDING_MERCHANT)));
    }

    @Test
    void auditMerchant_reject_freshApplication() throws Exception {
        MvcResult apply = expectOk(doPost("/api/auth/merchant-apply", tokenOf(USER2), body(
                "shopName", unique("IT新店"), "contactPhone", "13900000000"))).andReturn();
        int merchantId = readJson(apply, "$.data.merchantId");
        expectOk(doPut("/api/admin/merchants/" + merchantId + "/audit", tokenOf(ADMIN),
                body("approve", false, "remark", "资料不全")))
                .andExpect(jsonPath("$.data.auditStatus").value(2));
    }

    @Test
    void auditMerchant_alreadyAudited_400() throws Exception {
        expectOk(doPut("/api/admin/merchants/3/audit", tokenOf(ADMIN), body("approve", true)));
        expectError(doPut("/api/admin/merchants/3/audit", tokenOf(ADMIN), body("approve", true)), 400);
    }

    @Test
    void auditMerchant_missingApprove_400() throws Exception {
        expectError(doPut("/api/admin/merchants/3/audit", tokenOf(ADMIN), body()), 400);
    }

    @Test
    void auditMerchant_notFound_404() throws Exception {
        expectError(doPut("/api/admin/merchants/99999/audit", tokenOf(ADMIN), body("approve", true)), 404);
    }

    // 8.1 待审商品列表
    @Test
    void listPendingProducts_containsSeed() throws Exception {
        expectOk(doGet("/api/admin/products/pending", tokenOf(ADMIN)))
                .andExpect(jsonPath("$.data[?(@.id == 8)]").isNotEmpty());
    }

    // 8.2 待审商品详情
    @Test
    void productDetail_seedPending() throws Exception {
        expectOk(doGet("/api/admin/products/8", tokenOf(ADMIN)))
                .andExpect(jsonPath("$.data.status").value(0))
                .andExpect(jsonPath("$.data.skus[0].id").isNumber());
    }

    @Test
    void productDetail_notFound_404() throws Exception {
        expectError(doGet("/api/admin/products/99999", tokenOf(ADMIN)), 404);
    }

    // 8.3 商品审核
    @Test
    void auditProduct_approve_seedPending() throws Exception {
        expectOk(doPut("/api/admin/products/8/audit", tokenOf(ADMIN), body("approve", true)))
                .andExpect(jsonPath("$.data.status").value(1));
        // 审核通过后公开可见
        expectOk(doGet("/api/products/8", null));
    }

    @Test
    void auditProduct_reject_freshProduct() throws Exception {
        MvcResult create = expectOk(doPost("/api/merchant/products", tokenOf(MERCHANT2),
                productBody(311L, unique("IT商品")))).andReturn();
        int productId = readJson(create, "$.data.productId");
        expectOk(doPut("/api/admin/products/" + productId + "/audit", tokenOf(ADMIN),
                body("approve", false, "remark", "图片不合规")))
                .andExpect(jsonPath("$.data.status").value(3));
    }

    @Test
    void auditProduct_alreadyAudited_400() throws Exception {
        expectOk(doPut("/api/admin/products/8/audit", tokenOf(ADMIN), body("approve", true)));
        expectError(doPut("/api/admin/products/8/audit", tokenOf(ADMIN), body("approve", true)), 400);
    }

    @Test
    void auditProduct_missingApprove_400() throws Exception {
        expectError(doPut("/api/admin/products/8/audit", tokenOf(ADMIN), body()), 400);
    }
}
