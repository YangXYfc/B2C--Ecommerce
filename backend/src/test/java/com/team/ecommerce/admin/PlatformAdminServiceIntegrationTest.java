package com.team.ecommerce.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.team.ecommerce.admin.dto.BannerRequest;
import com.team.ecommerce.admin.dto.BannerView;
import com.team.ecommerce.admin.mapper.PlatformAdminLogMapper;
import com.team.ecommerce.admin.service.PlatformAdminService;
import com.team.ecommerce.common.error.BusinessException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PlatformAdminServiceIntegrationTest {

    @Autowired
    private PlatformAdminService platformAdminService;

    @Autowired
    private PlatformAdminLogMapper adminLogMapper;

    private static final Long ADMIN_ID = 1L;

    @Test
    void dashboardReturnsCounts() {
        var dashboard = platformAdminService.dashboard(ADMIN_ID);
        // data.sql: 7 users, 3 merchants, 6 orders
        assertEquals(7, dashboard.userCount());
        assertEquals(3, dashboard.merchantCount());
        assertEquals(6, dashboard.orderCount());
        // sales = sum of pay_amount for status 1,2,3,4
        assertTrue(dashboard.salesAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void listEnabledBannersOnlyReturnsActive() {
        var banners = platformAdminService.listEnabledBanners();
        // data.sql: 4 banners all enabled
        assertEquals(4, banners.size());
        assertTrue(banners.stream().allMatch(BannerView::enabled));
    }

    @Test
    void createBannerAndLog() {
        var banner = platformAdminService.createBanner(ADMIN_ID,
                new BannerRequest("测试轮播", "http://img/x.png", "/promo/x", 99, true));
        assertNotNull(banner.id());
        assertEquals("测试轮播", banner.title());

        var logs = adminLogMapper.selectPage("BANNER_CREATE", 0, 10);
        assertEquals(1, logs.size());
    }

    @Test
    void updateBanner() {
        var banner = platformAdminService.updateBanner(ADMIN_ID, 1L,
                new BannerRequest("新标题", "http://img/y.png", "/promo/y", 1, false));
        assertEquals("新标题", banner.title());
        assertEquals(false, banner.enabled());
    }

    @Test
    void deleteBannerAndLog() {
        platformAdminService.deleteBanner(ADMIN_ID, 4L);
        var all = platformAdminService.listAllBanners(ADMIN_ID);
        assertEquals(3, all.size());

        var logs = adminLogMapper.selectPage("BANNER_DELETE", 0, 10);
        assertEquals(1, logs.size());
    }

    @Test
    void deleteMissingBannerFails() {
        assertThrows(BusinessException.class, () -> platformAdminService.deleteBanner(ADMIN_ID, 999L));
    }
}
