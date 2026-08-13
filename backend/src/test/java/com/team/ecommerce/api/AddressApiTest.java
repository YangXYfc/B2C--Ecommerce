package com.team.ecommerce.api;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 收货地址接口集成测试（契约第 2 节）。
 * 种子数据：user1(4) 有地址 1(默认)、2；user2(5) 有地址 3。
 */
class AddressApiTest extends AbstractApiTest {

    // 2.1 列表
    @Test
    void list_returnsOwnAddresses() throws Exception {
        expectOk(doGet("/api/addresses", tokenOf(USER1)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].isDefault").value(1))
                .andExpect(jsonPath("$.data[1].isDefault").value(0));
    }

    // 2.2 新增
    @Test
    void add_success_returnsId() throws Exception {
        expectOk(doPost("/api/addresses", tokenOf(USER1), addressBody()))
                .andExpect(jsonPath("$.data.id").isNumber());
    }

    @Test
    void add_isDefaultClearsOthers() throws Exception {
        expectOk(doPost("/api/addresses", tokenOf(USER1), addressBody(1)));
        expectOk(doGet("/api/addresses", tokenOf(USER1)))
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[0].isDefault").value(1))
                .andExpect(jsonPath("$.data[1].isDefault").value(0))
                .andExpect(jsonPath("$.data[2].isDefault").value(0));
    }

    @Test
    void add_missingField_400() throws Exception {
        Map<String, Object> a = addressBody();
        a.remove("detail");
        expectError(doPost("/api/addresses", tokenOf(USER1), a), 400);
    }

    // 2.3 编辑
    @Test
    void update_ownAddress_success() throws Exception {
        Map<String, Object> a = addressBody();
        a.put("detail", "新详细地址");
        expectOk(doPut("/api/addresses/1", tokenOf(USER1), a))
                .andExpect(jsonPath("$.data.detail").value("新详细地址"));
    }

    @Test
    void update_otherAddress_403() throws Exception {
        expectError(doPut("/api/addresses/3", tokenOf(USER1), addressBody()), 403);
    }

    @Test
    void update_notFound_404() throws Exception {
        expectError(doPut("/api/addresses/99999", tokenOf(USER1), addressBody()), 404);
    }

    // 2.4 删除
    @Test
    void delete_ownAddress_success() throws Exception {
        expectOk(doDelete("/api/addresses/1", tokenOf(USER1)))
                .andExpect(jsonPath("$.data").value(nullValue()));
        expectOk(doGet("/api/addresses", tokenOf(USER1)))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    void delete_otherAddress_403() throws Exception {
        expectError(doDelete("/api/addresses/3", tokenOf(USER1)), 403);
    }

    @Test
    void delete_notFound_404() throws Exception {
        expectError(doDelete("/api/addresses/99999", tokenOf(USER1)), 404);
    }

    // 2.5 设为默认
    @Test
    void setDefault_success() throws Exception {
        expectOk(doPut("/api/addresses/2/default", tokenOf(USER1), null))
                .andExpect(jsonPath("$.data.isDefault").value(1));
        expectOk(doGet("/api/addresses", tokenOf(USER1)))
                .andExpect(jsonPath("$.data[0].isDefault").value(1))
                .andExpect(jsonPath("$.data[1].isDefault").value(0));
    }

    @Test
    void setDefault_otherAddress_403() throws Exception {
        expectError(doPut("/api/addresses/3/default", tokenOf(USER1), null), 403);
    }

    private Map<String, Object> addressBody() {
        return addressBody(0);
    }

    private Map<String, Object> addressBody(int isDefault) {
        return body("name", "张三", "phone", "13800000004", "province", "北京市",
                "city", "北京市", "district", "朝阳区", "detail", "测试地址1号", "isDefault", isDefault);
    }
}
