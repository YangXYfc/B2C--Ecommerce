package com.team.ecommerce.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.team.ecommerce.security.JwtUtil;
import org.hamcrest.Matcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 集成测试基类：全上下文 MockMvc（真实 DispatcherServlet + JwtAuthInterceptor + MyBatis + MySQL 测试库）。
 * 每个测试方法 {@link Transactional} 回滚，从种子数据基线出发，测试之间零干扰。
 *
 * <p>数据库初始化由 {@code com.team.ecommerce.support.TestDbBootstrap} 在上下文刷新时完成。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class AbstractApiTest {

    // 种子账号（密码均为 123456，见 data.sql）
    public static final String ADMIN = "admin";
    public static final String MERCHANT1 = "merchant1";
    public static final String MERCHANT2 = "merchant2";
    public static final String PENDING_MERCHANT = "merchant3"; // 待审核商家（audit_status=0）
    public static final String USER1 = "user1";
    public static final String USER2 = "user2";
    public static final String USER3 = "user3";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtUtil jwtUtil;

    private static final Map<String, String> TOKENS = new ConcurrentHashMap<>();

    // ---------- 请求助手（token 为 null 表示不带鉴权头） ----------

    protected ResultActions doGet(String url, String token) throws Exception {
        return perform(get(url), token);
    }

    /**
     * 带查询参数的 GET。参数走 {@code MockHttpServletRequestBuilder#param} 传入原始值，
     * 避免把中文/特殊字符预编码进 URL（MockMvc 会按 ISO-8859-1 解码 query string，导致中文乱码）。
     * 用法：doGet("/api/products", null, "keyword", "手机")
     */
    protected ResultActions doGet(String url, String token, String... params) throws Exception {
        MockHttpServletRequestBuilder b = get(url);
        for (int i = 0; i + 1 < params.length; i += 2) {
            b.param(params[i], params[i + 1]);
        }
        return perform(b, token);
    }

    protected ResultActions doPost(String url, String token, Object body) throws Exception {
        MockHttpServletRequestBuilder b = post(url);
        if (body != null) {
            b = b.contentType(MediaType.APPLICATION_JSON).content(json(body));
        }
        return perform(b, token);
    }

    protected ResultActions doPut(String url, String token, Object body) throws Exception {
        MockHttpServletRequestBuilder b = put(url);
        if (body != null) {
            b = b.contentType(MediaType.APPLICATION_JSON).content(json(body));
        }
        return perform(b, token);
    }

    protected ResultActions doDelete(String url, String token) throws Exception {
        return perform(delete(url), token);
    }

    private ResultActions perform(MockHttpServletRequestBuilder b, String token) throws Exception {
        if (token != null) {
            b.header("Authorization", "Bearer " + token);
        }
        return mockMvc.perform(b);
    }

    // ---------- 断言助手 ----------

    /** 断言 HTTP 状态与业务 code 均为 200。 */
    protected ResultActions expectOk(ResultActions ra) throws Exception {
        return ra.andExpect(status().isOk()).andExpect(jsonPath("$.code").value(anyOf(is(200), is("SUCCESS"))));
    }

    /** 断言 HTTP 状态与业务 code 均为指定值（GlobalExceptionHandler 保证二者一致），且 data 为 null。 */
    protected ResultActions expectError(ResultActions ra, int code) throws Exception {
        Matcher<?> codeMatcher = code == 400 ? anyOf(is(400), is("INVALID_ARGUMENT")) : is(code);
        return ra.andExpect(status().is(code)).andExpect(jsonPath("$.code").value(codeMatcher))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    // ---------- 数据助手 ----------

    /** 通过真实登录接口获取并缓存 JWT（登录是只读操作，跨测试安全）。 */
    protected String tokenOf(String username) {
        return TOKENS.computeIfAbsent(username, u -> {
            try {
                MvcResult result = mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(Map.of("username", u, "password", "123456"))))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value(200))
                        .andReturn();
                return readJson(result, "$.data.token");
            } catch (Exception e) {
                throw new IllegalStateException("种子账号登录失败: " + u, e);
            }
        });
    }

    protected String json(Object body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    protected <T> T readJson(MvcResult result, String path) {
        // getContentAsString(Charset) 声明了检查异常，改从字节数组解码更干净
        String body = new String(result.getResponse().getContentAsByteArray(), StandardCharsets.UTF_8);
        return JsonPath.read(body, path);
    }

    /** 生成不重复的名字（用户名/商品名/店铺名），避免与种子数据及测试间冲突。 */
    protected String unique(String prefix) {
        return prefix + System.nanoTime();
    }

    /** 便捷构建请求体 Map（避免 Map.of 混合类型推断问题）。 */
    protected Map<String, Object> body(Object... kv) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            m.put((String) kv[i], kv[i + 1]);
        }
        return m;
    }

    /** 单个 SKU 请求体。 */
    protected Map<String, Object> sku(String skuName, Object price, Object stock) {
        return body("skuName", skuName, "price", price, "originalPrice", price,
                "stock", stock, "attributes", Map.of("颜色", "黑"));
    }

    /** 商品请求体（含一个默认 SKU）。 */
    protected Map<String, Object> productBody(Long categoryId, String name) {
        return productBody(categoryId, name, List.of(sku("SKU-A", new java.math.BigDecimal("99.00"), 10)));
    }

    /** 商品请求体（自定义 SKU 列表）。 */
    protected Map<String, Object> productBody(Long categoryId, String name, List<Map<String, Object>> skus) {
        return body("categoryId", categoryId, "name", name, "subtitle", "副标题",
                "mainImage", "http://img.test/i.png", "subImages", List.of("http://img.test/a.png"),
                "description", "描述", "detailHtml", "<p>详情</p>", "skus", skus);
    }
}
