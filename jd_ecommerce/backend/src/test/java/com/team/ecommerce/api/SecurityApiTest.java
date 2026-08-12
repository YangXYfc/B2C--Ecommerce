package com.team.ecommerce.api;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 跨切面鉴权测试：公开接口免登录、受保护接口 401、角色路径 403。
 * 覆盖契约「鉴权：Authorization Bearer；除注册/登录外均需携带；merchant/**、admin/** 角色限制」。
 */
class SecurityApiTest extends AbstractApiTest {

    // 公开接口（WebConfig 放行的 excludePathPatterns）无需 token
    @Test
    void publicEndpoints_openWithoutToken() throws Exception {
        expectOk(doGet("/api/categories", null));
        expectOk(doGet("/api/products", null));
        expectOk(doGet("/api/products/1", null));
        expectOk(doPost("/api/auth/login", null, java.util.Map.of("username", "user1", "password", "123456")));
    }

    // 受保护接口缺 token → 401
    @Test
    void protectedEndpoints_noToken_401() throws Exception {
        expectError(doGet("/api/addresses", null), 401);
        expectError(doGet("/api/merchant/shop", null), 401);
        expectError(doGet("/api/admin/users", null), 401);
    }

    // 非法 token → 401
    @Test
    void garbageToken_401() throws Exception {
        expectError(doGet("/api/addresses", "not.a.jwt"), 401);
    }

    // token 对应不存在的用户 → 401「账号不存在」（拦截器查库失败）
    @Test
    void tokenOfNonexistentUser_401() throws Exception {
        String ghost = jwtUtil.generateToken(999999L, "USER");
        expectError(doGet("/api/addresses", ghost), 401);
    }

    // USER 访问商家/平台管理路径 → 403
    @Test
    void userOnMerchantAndAdmin_403() throws Exception {
        expectError(doGet("/api/merchant/shop", tokenOf(USER1)), 403);
        expectError(doGet("/api/admin/users", tokenOf(USER1)), 403);
    }

    // MERCHANT 访问平台管理路径 → 403
    @Test
    void merchantOnAdmin_403() throws Exception {
        expectError(doGet("/api/admin/users", tokenOf(MERCHANT1)), 403);
    }

    // ADMIN 访问商家路径 → 403（角色互斥，非 MERCHANT）
    @Test
    void adminOnMerchant_403() throws Exception {
        expectError(doGet("/api/merchant/shop", tokenOf(ADMIN)), 403);
    }

    // token 角色声明被篡改不授予任何特权：拦截器按数据库中真实角色拦截（不信 token 里的 role 声明）
    @Test
    void forgedRoleClaim_noPrivilege() throws Exception {
        // user1(4) 实际角色是 USER，token 里伪造 role=MERCHANT 也不生效
        String forged = jwtUtil.generateToken(4L, "MERCHANT");
        expectError(doGet("/api/admin/users", forged), 403);
        expectError(doGet("/api/merchant/shop", forged), 403);
        // 仅普通登录接口可用
        expectOk(doGet("/api/addresses", forged));
    }
}
