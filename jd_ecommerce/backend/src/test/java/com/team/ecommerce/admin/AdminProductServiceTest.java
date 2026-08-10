package com.team.ecommerce.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.ecommerce.admin.dto.AdminProductDetailVO;
import com.team.ecommerce.admin.dto.AdminProductPendingVO;
import com.team.ecommerce.admin.dto.ProductAuditVO;
import com.team.ecommerce.admin.service.AdminProductService;
import com.team.ecommerce.auth.entity.Merchant;
import com.team.ecommerce.auth.mapper.MerchantMapper;
import com.team.ecommerce.catalog.entity.Product;
import com.team.ecommerce.catalog.entity.ProductSku;
import com.team.ecommerce.catalog.mapper.ProductMapper;
import com.team.ecommerce.catalog.mapper.ProductSkuMapper;
import com.team.ecommerce.common.BizException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminProductServiceTest {

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductSkuMapper productSkuMapper;

    @Mock
    private MerchantMapper merchantMapper;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AdminProductService adminProductService;

    private Product product(long id, long merchantId, int status) {
        Product p = new Product();
        p.setId(id);
        p.setMerchantId(merchantId);
        p.setCategoryId(312L);
        p.setName("春秋夹克外套 男款防风");
        p.setSubtitle("休闲百搭 | 轻薄防风");
        p.setMainImage("http://img/main.jpg");
        p.setSubImages("[\"http://img/sub1.jpg\"]");
        p.setDescription("防风面料");
        p.setDetailHtml("<p>详情</p>");
        p.setPrice(new BigDecimal("199.00"));
        p.setStatus(status);
        p.setSalesCount(0);
        p.setCreatedAt(LocalDateTime.of(2026, 7, 2, 14, 0));
        return p;
    }

    private ProductSku sku(long id, long productId) {
        ProductSku s = new ProductSku();
        s.setId(id);
        s.setProductId(productId);
        s.setSkuName("夹克 黑色 M");
        s.setPrice(new BigDecimal("199.00"));
        s.setOriginalPrice(new BigDecimal("299.00"));
        s.setStock(100);
        s.setAttributes("{\"颜色\":\"黑色\",\"尺码\":\"M\"}");
        s.setSkuImage("http://img/sku.jpg");
        s.setStatus(1);
        return s;
    }

    private Merchant merchant(long id, String shopName) {
        Merchant m = new Merchant();
        m.setId(id);
        m.setShopName(shopName);
        return m;
    }

    @Test
    void listPending_mapsRowsWithShopName() {
        when(productMapper.findPending()).thenReturn(List.of(product(8, 2, 0)));
        when(merchantMapper.findById(2L)).thenReturn(merchant(2, "服饰优选店"));

        List<AdminProductPendingVO> list = adminProductService.listPending();

        assertEquals(1, list.size());
        AdminProductPendingVO vo = list.get(0);
        assertEquals(8L, vo.id());
        assertEquals("春秋夹克外套 男款防风", vo.name());
        assertEquals(new BigDecimal("199.00"), vo.price());
        assertEquals(0, vo.status());
        assertEquals(2L, vo.merchantId());
        assertEquals("服饰优选店", vo.shopName());
        assertEquals(312L, vo.categoryId());
        assertEquals(LocalDateTime.of(2026, 7, 2, 14, 0), vo.createdAt());
    }

    @Test
    void detail_found_mapsAllFields() {
        when(productMapper.findById(8L)).thenReturn(product(8, 2, 0));
        when(merchantMapper.findById(2L)).thenReturn(merchant(2, "服饰优选店"));
        when(productSkuMapper.findByProductId(8L)).thenReturn(List.of(sku(20, 8)));

        AdminProductDetailVO vo = adminProductService.detail(8L);

        assertEquals(8L, vo.id());
        assertEquals("春秋夹克外套 男款防风", vo.name());
        assertEquals(1, vo.subImages().size());
        assertEquals("http://img/sub1.jpg", vo.subImages().get(0));
        assertEquals(new BigDecimal("199.00"), vo.price());
        assertEquals(0, vo.status());
        assertEquals(2L, vo.merchantId());
        assertEquals("服饰优选店", vo.shopName());
        assertEquals(312L, vo.categoryId());
        assertEquals(1, vo.skus().size());
        assertEquals(20L, vo.skus().get(0).id());
        assertEquals("黑色", vo.skus().get(0).attributes().get("颜色"));
        assertEquals("M", vo.skus().get(0).attributes().get("尺码"));
        assertEquals("http://img/sku.jpg", vo.skus().get(0).skuImage());
    }

    @Test
    void detail_notFound_throwsNotFound() {
        when(productMapper.findById(999L)).thenReturn(null);

        BizException e = assertThrows(BizException.class, () -> adminProductService.detail(999L));
        assertEquals(404, e.getCode());
    }

    @Test
    void audit_approve_updatesStatus1() {
        when(productMapper.findById(8L)).thenReturn(product(8, 2, 0));

        ProductAuditVO vo = adminProductService.audit(8L, true, "商品信息完整，审核通过");

        assertEquals(8L, vo.productId());
        assertEquals(1, vo.status());
        assertEquals("商品信息完整，审核通过", vo.remark());
        verify(productMapper).updateStatus(8L, 1, "商品信息完整，审核通过");
    }

    @Test
    void audit_reject_updatesStatus3() {
        when(productMapper.findById(8L)).thenReturn(product(8, 2, 0));

        ProductAuditVO vo = adminProductService.audit(8L, false, "图片不合格");

        assertEquals(8L, vo.productId());
        assertEquals(3, vo.status());
        assertEquals("图片不合格", vo.remark());
        verify(productMapper).updateStatus(8L, 3, "图片不合格");
    }

    @Test
    void audit_approveNull_throwsBadRequest() {
        BizException e = assertThrows(BizException.class, () -> adminProductService.audit(8L, null, "x"));
        assertEquals(400, e.getCode());
    }

    @Test
    void audit_notFound_throwsNotFound() {
        when(productMapper.findById(999L)).thenReturn(null);

        BizException e = assertThrows(BizException.class, () -> adminProductService.audit(999L, true, "x"));
        assertEquals(404, e.getCode());
    }

    @Test
    void audit_alreadyProcessed_throwsBadRequest() {
        when(productMapper.findById(8L)).thenReturn(product(8, 2, 1));

        BizException e = assertThrows(BizException.class, () -> adminProductService.audit(8L, true, "x"));
        assertEquals(400, e.getCode());
        verify(productMapper, never()).updateStatus(any(), any(), any());
    }
}
