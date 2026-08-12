package com.team.ecommerce.api;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 商家店铺接口集成测试（契约第 5 节）。
 * 注意：ShopService 只要求商家记录存在（不校验审核状态），待审核商家 merchant3 也能访问店铺。
 */
class ShopApiTest extends AbstractApiTest {

    // 5.1 店铺资料
    @Test
    void get_success_returnsShop() throws Exception {
        expectOk(doGet("/api/merchant/shop", tokenOf(MERCHANT1)))
                .andExpect(jsonPath("$.data.shopName").value("数码旗舰店"))
                .andExpect(jsonPath("$.data.auditStatus").value(1));
    }

    @Test
    void get_pendingMerchant_allowed() throws Exception {
        expectOk(doGet("/api/merchant/shop", tokenOf(PENDING_MERCHANT)))
                .andExpect(jsonPath("$.data.shopName").value("待审核商家"))
                .andExpect(jsonPath("$.data.auditStatus").value(0));
    }

    @Test
    void get_nonMerchant_403() throws Exception {
        expectError(doGet("/api/merchant/shop", tokenOf(USER1)), 403);
    }

    // 5.2 更新店铺
    @Test
    void update_success() throws Exception {
        String newName = unique("IT新店");
        expectOk(doPut("/api/merchant/shop", tokenOf(MERCHANT1), body(
                "shopName", newName, "shopLogo", "http://img.test/logo.png",
                "description", "新描述", "contactPhone", "13900000000")))
                .andExpect(jsonPath("$.data.shopName").value(newName))
                .andExpect(jsonPath("$.data.auditStatus").value(1));
    }

    @Test
    void update_pendingMerchant_allowed() throws Exception {
        expectOk(doPut("/api/merchant/shop", tokenOf(PENDING_MERCHANT), body(
                "shopName", unique("IT新店"), "contactPhone", "13900000000")))
                .andExpect(jsonPath("$.data.auditStatus").value(0));
    }

    @Test
    void update_nonMerchant_403() throws Exception {
        expectError(doPut("/api/merchant/shop", tokenOf(USER1), body(
                "shopName", "x", "contactPhone", "13900000000")), 403);
    }

    @Test
    void update_missingRequired_400() throws Exception {
        expectError(doPut("/api/merchant/shop", tokenOf(MERCHANT1), body(
                "shopName", "", "contactPhone", "")), 400);
    }
}
