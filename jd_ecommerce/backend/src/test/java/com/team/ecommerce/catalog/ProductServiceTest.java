package com.team.ecommerce.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.ecommerce.auth.entity.Merchant;
import com.team.ecommerce.auth.mapper.MerchantMapper;
import com.team.ecommerce.catalog.dto.ProductDetailVO;
import com.team.ecommerce.catalog.dto.ProductIdVO;
import com.team.ecommerce.catalog.dto.ProductRequest;
import com.team.ecommerce.catalog.dto.ProductStatusVO;
import com.team.ecommerce.catalog.dto.SkuRequest;
import com.team.ecommerce.catalog.dto.SkuVO;
import com.team.ecommerce.catalog.dto.StockVO;
import com.team.ecommerce.catalog.entity.Category;
import com.team.ecommerce.catalog.entity.Product;
import com.team.ecommerce.catalog.entity.ProductSku;
import com.team.ecommerce.catalog.mapper.CategoryMapper;
import com.team.ecommerce.catalog.mapper.ProductMapper;
import com.team.ecommerce.catalog.mapper.ProductSkuMapper;
import com.team.ecommerce.catalog.service.ProductService;
import com.team.ecommerce.common.BizException;
import com.team.ecommerce.common.PageResult;
import com.team.ecommerce.security.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductMapper productMapper;
    @Mock
    private ProductSkuMapper productSkuMapper;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private MerchantMapper merchantMapper;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        // 商家账号 merchant1（user_id=2）
        UserContext.set(new UserContext.LoginUser(2L, "merchant1", "MERCHANT"));
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    // ---------- 测试数据 ----------

    private Merchant approvedMerchant() {
        Merchant m = new Merchant();
        m.setId(1L);
        m.setUserId(2L);
        m.setShopName("数码旗舰店");
        m.setAuditStatus(1);
        return m;
    }

    private Product product(long id, long merchantId) {
        Product p = new Product();
        p.setId(id);
        p.setMerchantId(merchantId);
        p.setCategoryId(111L);
        p.setName("智选 Pro 5G 手机 12GB+256GB");
        p.setSubtitle("旗舰芯片 | 120W快充");
        p.setMainImage("http://img/main.jpg");
        p.setPrice(new BigDecimal("4999.00"));
        p.setStatus(1);
        p.setAuditRemark(null);
        p.setSalesCount(100);
        return p;
    }

    private ProductSku sku(long id, long productId) {
        ProductSku s = new ProductSku();
        s.setId(id);
        s.setProductId(productId);
        s.setSkuName("智选Pro 5G 钛空灰 12GB+256GB");
        s.setPrice(new BigDecimal("4999.00"));
        s.setOriginalPrice(new BigDecimal("5499.00"));
        s.setStock(500);
        s.setAttributes("{\"颜色\":\"钛空灰\",\"版本\":\"12GB+256GB\"}");
        s.setStatus(1);
        return s;
    }

    private Category category(long id, long parentId) {
        Category c = new Category();
        c.setId(id);
        c.setParentId(parentId);
        c.setName("分类" + id);
        c.setStatus(1);
        return c;
    }

    private List<Category> seedCategories() {
        return List.of(category(1, 0), category(11, 1), category(111, 11));
    }

    private ProductRequest request(SkuRequest... skus) {
        return new ProductRequest(111L, "智选 Pro 5G 手机 12GB+256GB", "旗舰芯片 | 120W快充",
                "http://img/main.jpg", List.of("http://img/sub1.jpg"), "描述", "<p>详情</p>", List.of(skus));
    }

    // ---------- 4.1 公开列表 ----------

    @Test
    void list_defaultPagination_mapsRows() {
        when(productMapper.countPublic(eq(1), eq("手机"), isNull())).thenReturn(2L);
        when(productMapper.findPublicPage(eq(1), eq("手机"), isNull(), eq("default"), eq(0), eq(10)))
                .thenReturn(List.of(product(1, 1), product(2, 1)));

        PageResult<com.team.ecommerce.catalog.dto.ProductListVO> result =
                productService.list("手机", null, "default", null, null);

        assertEquals(2L, result.total());
        assertEquals(1, result.page());
        assertEquals(10, result.size());
        assertEquals(2, result.list().size());
        assertEquals("智选 Pro 5G 手机 12GB+256GB", result.list().get(0).name());
    }

    @Test
    void list_sizeOverMax_throwsBadRequest() {
        BizException e = assertThrows(BizException.class,
                () -> productService.list(null, null, null, 1, 101));
        assertEquals(400, e.getCode());
    }

    @Test
    void list_categoryIncludesDescendants() {
        when(categoryMapper.findByStatus(1)).thenReturn(seedCategories());
        when(productMapper.countPublic(eq(1), isNull(), anyList())).thenReturn(2L);
        when(productMapper.findPublicPage(eq(1), isNull(), anyList(), eq("default"), eq(0), eq(10)))
                .thenReturn(List.of(product(1, 1)));

        productService.list(null, 11L, "default", 1, 10);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Long>> captor = ArgumentCaptor.forClass(List.class);
        verify(productMapper).countPublic(eq(1), isNull(), captor.capture());
        assertEquals(List.of(11L, 111L), captor.getValue());
    }

    @Test
    void list_categoryNoProducts_returnsEmptyPage() {
        when(categoryMapper.findByStatus(1)).thenReturn(seedCategories());
        when(productMapper.countPublic(eq(1), isNull(), anyList())).thenReturn(0L);
        when(productMapper.findPublicPage(eq(1), isNull(), anyList(), eq("default"), eq(0), eq(10)))
                .thenReturn(List.of());

        PageResult<com.team.ecommerce.catalog.dto.ProductListVO> result =
                productService.list(null, 999L, "default", 1, 10);

        assertEquals(0L, result.total());
        assertEquals(0, result.list().size());
    }

    // ---------- 4.2 公开详情 ----------

    @Test
    void detail_onsale_returnsFullDetail() {
        when(productMapper.findById(1L)).thenReturn(product(1, 1));
        when(merchantMapper.findById(1L)).thenReturn(approvedMerchant());
        when(categoryMapper.findById(111L)).thenReturn(category(111, 11));
        when(productSkuMapper.findByProductId(1L)).thenReturn(List.of(sku(1, 1)));

        ProductDetailVO vo = productService.detail(1L);

        assertEquals("数码旗舰店", vo.merchantName());
        assertEquals("分类111", vo.categoryName());
        assertEquals(1, vo.skus().size());
        SkuVO sku = vo.skus().get(0);
        assertEquals("钛空灰", sku.attributes().get("颜色"));
        assertEquals("12GB+256GB", sku.attributes().get("版本"));
    }

    @Test
    void detail_offShelf_throwsNotFound() {
        Product p = product(1, 1);
        p.setStatus(2);
        when(productMapper.findById(1L)).thenReturn(p);

        BizException e = assertThrows(BizException.class, () -> productService.detail(1L));
        assertEquals(404, e.getCode());
    }

    @Test
    void detail_notExists_throwsNotFound() {
        when(productMapper.findById(1L)).thenReturn(null);

        BizException e = assertThrows(BizException.class, () -> productService.detail(1L));
        assertEquals(404, e.getCode());
    }

    // ---------- 4.6 发布商品 ----------

    @Test
    void create_approvedMerchant_insertsProductAndSkus() {
        when(merchantMapper.findByUserId(2L)).thenReturn(approvedMerchant());
        when(categoryMapper.findById(111L)).thenReturn(category(111, 11));
        when(productMapper.insert(any(Product.class))).thenAnswer(inv -> {
            inv.getArgument(0, Product.class).setId(10L);
            return 1;
        });
        when(productSkuMapper.insert(any(ProductSku.class))).thenReturn(1);

        ProductRequest req = request(
                new SkuRequest("SKU-A", new BigDecimal("4999.00"), new BigDecimal("5499.00"), 500, null, null),
                new SkuRequest("SKU-B", new BigDecimal("3999.00"), null, 100, null, null));

        ProductIdVO vo = productService.create(req);

        assertEquals(10L, vo.productId());
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productMapper).insert(captor.capture());
        Product inserted = captor.getValue();
        assertEquals(0, inserted.getStatus());
        assertEquals(0, new BigDecimal("3999.00").compareTo(inserted.getPrice()));
        assertEquals(0, inserted.getSalesCount());
        assertEquals(1L, inserted.getMerchantId());
        assertEquals("[\"http://img/sub1.jpg\"]", inserted.getSubImages());
        verify(productSkuMapper, times(2)).insert(any(ProductSku.class));
    }

    @Test
    void create_emptySkus_throwsBadRequest() {
        when(merchantMapper.findByUserId(2L)).thenReturn(approvedMerchant());
        when(categoryMapper.findById(111L)).thenReturn(category(111, 11));

        BizException e = assertThrows(BizException.class, () -> productService.create(request()));
        assertEquals(400, e.getCode());
    }

    @Test
    void create_unapprovedMerchant_throwsForbidden() {
        Merchant m = approvedMerchant();
        m.setAuditStatus(0);
        when(merchantMapper.findByUserId(2L)).thenReturn(m);

        BizException e = assertThrows(BizException.class,
                () -> productService.create(request(new SkuRequest("SKU", BigDecimal.TEN, null, 1, null, null))));
        assertEquals(403, e.getCode());
    }

    @Test
    void create_categoryNotFound_throwsBadRequest() {
        when(merchantMapper.findByUserId(2L)).thenReturn(approvedMerchant());
        when(categoryMapper.findById(111L)).thenReturn(null);

        BizException e = assertThrows(BizException.class,
                () -> productService.create(request(new SkuRequest("SKU", BigDecimal.TEN, null, 1, null, null))));
        assertEquals(400, e.getCode());
    }

    // ---------- 4.7 编辑商品 ----------

    @Test
    void update_owned_replacesSkusAndResetsStatus() {
        when(merchantMapper.findByUserId(2L)).thenReturn(approvedMerchant());
        when(productMapper.findById(1L)).thenReturn(product(1, 1));
        when(categoryMapper.findById(111L)).thenReturn(category(111, 11));
        when(productMapper.update(any(Product.class))).thenReturn(1);
        when(productSkuMapper.deleteByProductId(1L)).thenReturn(1);
        when(productSkuMapper.insert(any(ProductSku.class))).thenReturn(1);

        ProductStatusVO vo = productService.update(1L,
                request(new SkuRequest("SKU-A", new BigDecimal("4999.00"), null, 100, null, null)));

        assertEquals(1L, vo.productId());
        assertEquals(0, vo.status());
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productMapper).update(captor.capture());
        assertEquals(0, captor.getValue().getStatus());
        assertNull(captor.getValue().getAuditRemark());
        verify(productSkuMapper).deleteByProductId(1L);
        verify(productSkuMapper, times(1)).insert(any(ProductSku.class));
    }

    @Test
    void update_notOwner_throwsForbidden() {
        when(merchantMapper.findByUserId(2L)).thenReturn(approvedMerchant());
        when(productMapper.findById(1L)).thenReturn(product(1, 2));

        BizException e = assertThrows(BizException.class,
                () -> productService.update(1L, request(new SkuRequest("SKU", BigDecimal.TEN, null, 1, null, null))));
        assertEquals(403, e.getCode());
    }

    @Test
    void update_notExists_throwsNotFound() {
        when(merchantMapper.findByUserId(2L)).thenReturn(approvedMerchant());
        when(productMapper.findById(1L)).thenReturn(null);

        BizException e = assertThrows(BizException.class,
                () -> productService.update(1L, request(new SkuRequest("SKU", BigDecimal.TEN, null, 1, null, null))));
        assertEquals(404, e.getCode());
    }

    // ---------- 4.8 下架 ----------

    @Test
    void offShelf_owned_setsStatus2() {
        when(merchantMapper.findByUserId(2L)).thenReturn(approvedMerchant());
        when(productMapper.findById(1L)).thenReturn(product(1, 1));
        when(productMapper.updateStatus(1L, 2, null)).thenReturn(1);

        ProductStatusVO vo = productService.offShelf(1L);

        assertEquals(1L, vo.productId());
        assertEquals(2, vo.status());
        verify(productMapper).updateStatus(1L, 2, null);
    }

    @Test
    void offShelf_notOwner_throwsForbidden() {
        when(merchantMapper.findByUserId(2L)).thenReturn(approvedMerchant());
        when(productMapper.findById(1L)).thenReturn(product(1, 2));

        BizException e = assertThrows(BizException.class, () -> productService.offShelf(1L));
        assertEquals(403, e.getCode());
    }

    // ---------- 4.9 SKU 库存 ----------

    @Test
    void updateStock_owned_updatesStock() {
        when(merchantMapper.findByUserId(2L)).thenReturn(approvedMerchant());
        when(productSkuMapper.findById(1L)).thenReturn(sku(1, 1));
        when(productMapper.findById(1L)).thenReturn(product(1, 1));
        when(productSkuMapper.updateStock(1L, 600)).thenReturn(1);

        StockVO vo = productService.updateStock(1L, 600);

        assertEquals(1L, vo.skuId());
        assertEquals(600, vo.stock());
        verify(productSkuMapper).updateStock(1L, 600);
    }

    @Test
    void updateStock_negative_throwsBadRequest() {
        BizException e = assertThrows(BizException.class, () -> productService.updateStock(1L, -1));
        assertEquals(400, e.getCode());
    }

    @Test
    void updateStock_skuNotFound_throwsNotFound() {
        when(merchantMapper.findByUserId(2L)).thenReturn(approvedMerchant());
        when(productSkuMapper.findById(1L)).thenReturn(null);

        BizException e = assertThrows(BizException.class, () -> productService.updateStock(1L, 600));
        assertEquals(404, e.getCode());
    }

    @Test
    void updateStock_notOwner_throwsForbidden() {
        when(merchantMapper.findByUserId(2L)).thenReturn(approvedMerchant());
        when(productSkuMapper.findById(1L)).thenReturn(sku(1, 2));
        when(productMapper.findById(2L)).thenReturn(product(2, 2));

        BizException e = assertThrows(BizException.class, () -> productService.updateStock(1L, 600));
        assertEquals(403, e.getCode());
    }
}
