package com.team.ecommerce.api;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 账号接口集成测试（契约第 1 节：注册/登录/商家入驻/资料/改资料/改密）。
 */
class AuthApiTest extends AbstractApiTest {

    // 1.1 注册
    @Test
    void register_success_returnsUser_andCanLogin() throws Exception {
        String username = unique("ituser_");
        expectOk(doPost("/api/auth/register", null, body(
                "username", username, "password", "pass123456",
                "nickname", "测试用户")))
                .andExpect(jsonPath("$.data.username").value(username))
                .andExpect(jsonPath("$.data.role").value("USER"));

        expectOk(doPost("/api/auth/login", null, Map.of("username", username, "password", "pass123456")))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.user.username").value(username));
    }

    @Test
    void register_duplicateUsername_400() throws Exception {
        expectError(doPost("/api/auth/register", null, body("username", "user1", "password", "pass123456")), 400);
    }

    @Test
    void register_duplicatePhone_400() throws Exception {
        expectError(doPost("/api/auth/register", null, body(
                "username", unique("ituser_"), "password", "pass123456", "phone", "13800000004")), 400);
    }

    @Test
    void register_shortPassword_400() throws Exception {
        expectError(doPost("/api/auth/register", null, body(
                "username", unique("ituser_"), "password", "123")), 400);
    }

    // 1.2 登录
    @Test
    void login_success_returnsToken() throws Exception {
        expectOk(doPost("/api/auth/login", null, Map.of("username", "user1", "password", "123456")))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.user.role").value("USER"));
    }

    @Test
    void login_wrongPassword_401() throws Exception {
        expectError(doPost("/api/auth/login", null, Map.of("username", "user1", "password", "wrong")), 401);
    }

    @Test
    void login_unknownUser_401() throws Exception {
        expectError(doPost("/api/auth/login", null, Map.of("username", "nobody", "password", "123456")), 401);
    }

    @Test
    void login_disabledUser_403() throws Exception {
        // 管理员先禁用 user3（id=6），再登录应 403
        expectOk(doPut("/api/admin/users/6/status", tokenOf(ADMIN), body("status", 0)))
                .andExpect(jsonPath("$.data.status").value(0));
        expectError(doPost("/api/auth/login", null, Map.of("username", "user3", "password", "123456")), 403);
    }

    // 1.3 商家入驻
    @Test
    void merchantApply_success_returnsPending() throws Exception {
        MvcResult apply = expectOk(doPost("/api/auth/merchant-apply", tokenOf(USER1), body(
                "shopName", unique("IT新店"), "contactPhone", "13900000000", "description", "测试入驻")))
                .andReturn();
        int merchantId = readJson(apply, "$.data.merchantId");
        assertTrue(merchantId > 0);

        // 管理员待审列表应包含新申请
        expectOk(doGet("/api/admin/merchants/pending", tokenOf(ADMIN)))
                .andExpect(jsonPath("$.data[?(@.id == %d)]".formatted(merchantId)).isNotEmpty());
    }

    @Test
    void merchantApply_existingMerchant_400() throws Exception {
        expectError(doPost("/api/auth/merchant-apply", tokenOf(MERCHANT1), body(
                "shopName", "x", "contactPhone", "13900000000")), 400);
    }

    @Test
    void merchantApply_nonUserRole_403() throws Exception {
        expectError(doPost("/api/auth/merchant-apply", tokenOf(ADMIN), body(
                "shopName", "x", "contactPhone", "13900000000")), 403);
    }

    // 1.4 当前用户资料
    @Test
    void profile_returnsUserRole() throws Exception {
        expectOk(doGet("/api/auth/profile", tokenOf(USER1)))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andExpect(jsonPath("$.data.merchant").value(nullValue()));
    }

    @Test
    void profile_returnsMerchantNested() throws Exception {
        expectOk(doGet("/api/auth/profile", tokenOf(MERCHANT1)))
                .andExpect(jsonPath("$.data.role").value("MERCHANT"))
                .andExpect(jsonPath("$.data.merchant.shopName").value("数码旗舰店"))
                .andExpect(jsonPath("$.data.merchant.auditStatus").value(1));
    }

    // 1.5 更新资料
    @Test
    void updateProfile_success() throws Exception {
        expectOk(doPut("/api/users/profile", tokenOf(USER1), body("nickname", "新昵称")))
                .andExpect(jsonPath("$.data.nickname").value("新昵称"));
    }

    @Test
    void updateProfile_phoneCollision_400() throws Exception {
        expectError(doPut("/api/users/profile", tokenOf(USER1), body("phone", "13800000005")), 400);
    }

    @Test
    void updateProfile_empty_400() throws Exception {
        expectError(doPut("/api/users/profile", tokenOf(USER1), body()), 400);
    }

    // 1.6 修改密码
    @Test
    void changePassword_success_thenLoginWithNewPassword() throws Exception {
        expectOk(doPut("/api/users/password", tokenOf(USER1),
                body("oldPassword", "123456", "newPassword", "newpass123")))
                .andExpect(jsonPath("$.data").value(nullValue()));
        expectOk(doPost("/api/auth/login", null, Map.of("username", "user1", "password", "newpass123")));
    }

    @Test
    void changePassword_wrongOldPassword_400() throws Exception {
        expectError(doPut("/api/users/password", tokenOf(USER1),
                body("oldPassword", "wrong", "newPassword", "newpass123")), 400);
    }
}
