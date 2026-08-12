package com.team.ecommerce.api;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 公开分类 / 商品接口集成测试（契约第 3、4.1、4.2 节）。
 * 种子数据：19 个分类（4 顶级）；商品 1-7 上架、8 待审核、9 已拒绝。
 */
class CatalogApiTest extends AbstractApiTest {

    // 3.1 分类
    @Test
    void categories_returnsTopLevelTree() throws Exception {
        expectOk(doGet("/api/categories", null))
                .andExpect(jsonPath("$.data", hasSize(4)))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].children", hasSize(3)));
    }

    @Test
    void categories_byParentId() throws Exception {
        // 手机数码(id=1) 有 3 个子分类；食品生鲜(id=4) 无子分类
        expectOk(doGet("/api/categories?parentId=1", null))
                .andExpect(jsonPath("$.data", hasSize(3)));
        expectOk(doGet("/api/categories?parentId=4", null))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    // 4.1 商品列表
    @Test
    void products_returnsOnSalePaged() throws Exception {
        expectOk(doGet("/api/products", null))
                .andExpect(jsonPath("$.data.total").value(7))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.list", hasSize(7)));
    }

    @Test
    void products_keywordFilter() throws Exception {
        // "手机"命中上架商品 1、2；"夹克"只在待审核商品 8 中，不返回
        expectOk(doGet("/api/products", null, "keyword", "手机"))
                .andExpect(jsonPath("$.data.total").value(2));
        expectOk(doGet("/api/products", null, "keyword", "夹克"))
                .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    void products_categoryIdFilter_includesSubcategories() throws Exception {
        // 顶级分类 1(手机数码) 含其子分类全部上架商品：1,2,3,4
        expectOk(doGet("/api/products?categoryId=1", null))
                .andExpect(jsonPath("$.data.total").value(4));
        // 三级分类 111(智能手机)：商品 1,2
        expectOk(doGet("/api/products?categoryId=111", null))
                .andExpect(jsonPath("$.data.total").value(2));
    }

    @Test
    void products_sortPriceAsc() throws Exception {
        expectOk(doGet("/api/products?sort=priceAsc", null))
                .andExpect(jsonPath("$.data.list[0].id").value(5))
                .andExpect(jsonPath("$.data.list[0].price").value(59.0));
    }

    @Test
    void products_sizeOverMax_400() throws Exception {
        expectError(doGet("/api/products?size=101", null), 400);
    }

    // 4.2 商品详情
    @Test
    void productDetail_onSale() throws Exception {
        expectOk(doGet("/api/products/1", null))
                .andExpect(jsonPath("$.data.skus", hasSize(4)))
                .andExpect(jsonPath("$.data.merchantName").value("数码旗舰店"))
                .andExpect(jsonPath("$.data.categoryName").value("智能手机"))
                .andExpect(jsonPath("$.data.status").doesNotExist());
    }

    @Test
    void productDetail_pending_404() throws Exception {
        expectError(doGet("/api/products/8", null), 404);
    }

    @Test
    void productDetail_notFound_404() throws Exception {
        expectError(doGet("/api/products/99999", null), 404);
    }
}
