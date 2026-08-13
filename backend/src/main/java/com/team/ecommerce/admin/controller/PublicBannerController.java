package com.team.ecommerce.admin.controller;

import com.team.ecommerce.admin.dto.BannerView;
import com.team.ecommerce.admin.service.PlatformAdminService;
import com.team.ecommerce.common.api.ApiResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicBannerController {
    private final PlatformAdminService platformAdminService;
    public PublicBannerController(PlatformAdminService platformAdminService) { this.platformAdminService = platformAdminService; }

    @GetMapping("/api/banners")
    public ApiResponse<List<BannerView>> listEnabled() {
        return ApiResponse.success(platformAdminService.listEnabledBanners());
    }
}
