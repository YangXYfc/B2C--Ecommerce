package com.team.ecommerce.merchant;

import com.team.ecommerce.auth.entity.Merchant;
import com.team.ecommerce.auth.mapper.MerchantMapper;
import com.team.ecommerce.common.BizException;
import com.team.ecommerce.merchant.dto.ShopRequest;
import com.team.ecommerce.merchant.dto.ShopVO;
import com.team.ecommerce.merchant.service.ShopService;
import com.team.ecommerce.security.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    @Mock
    private MerchantMapper merchantMapper;

    @InjectMocks
    private ShopService shopService;

    @BeforeEach
    void setUp() {
        UserContext.set(new UserContext.LoginUser(2L, "merchant1", "MERCHANT"));
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    private Merchant merchant() {
        Merchant m = new Merchant();
        m.setId(1L);
        m.setUserId(2L);
        m.setShopName("数码旗舰店");
        m.setShopLogo("https://img.jd-demo.com/shop/logo1.png");
        m.setDescription("主营手机、电脑、数码配件，正品保障");
        m.setContactPhone("13800000002");
        m.setAuditStatus(1);
        return m;
    }

    @Test
    void get_merchantExists_returnsShopVO() {
        when(merchantMapper.findByUserId(2L)).thenReturn(merchant());

        ShopVO vo = shopService.get();

        assertEquals("数码旗舰店", vo.shopName());
        assertEquals("https://img.jd-demo.com/shop/logo1.png", vo.shopLogo());
        assertEquals("主营手机、电脑、数码配件，正品保障", vo.description());
        assertEquals("13800000002", vo.contactPhone());
        assertEquals(1, vo.auditStatus());
    }

    @Test
    void get_merchantNotFound_throwsForbidden() {
        when(merchantMapper.findByUserId(2L)).thenReturn(null);

        BizException e = assertThrows(BizException.class, () -> shopService.get());
        assertEquals(403, e.getCode());
    }

    @Test
    void update_setsFieldsAndPersists() {
        Merchant m = merchant();
        when(merchantMapper.findByUserId(2L)).thenReturn(m);
        when(merchantMapper.updateShop(m)).thenReturn(1);

        ShopRequest req = new ShopRequest("新店名", "https://img.jd-demo.com/shop/new.png", "新描述", "13900000000");
        ShopVO vo = shopService.update(req);

        assertEquals("新店名", vo.shopName());
        assertEquals("https://img.jd-demo.com/shop/new.png", vo.shopLogo());
        assertEquals("新描述", vo.description());
        assertEquals("13900000000", vo.contactPhone());
        assertEquals(1, vo.auditStatus());

        ArgumentCaptor<Merchant> captor = ArgumentCaptor.forClass(Merchant.class);
        verify(merchantMapper).updateShop(captor.capture());
        assertEquals("新店名", captor.getValue().getShopName());
    }

    @Test
    void update_merchantNotFound_throwsForbidden() {
        when(merchantMapper.findByUserId(2L)).thenReturn(null);

        BizException e = assertThrows(BizException.class,
                () -> shopService.update(new ShopRequest("新店名", null, null, "13900000000")));
        assertEquals(403, e.getCode());
    }
}
