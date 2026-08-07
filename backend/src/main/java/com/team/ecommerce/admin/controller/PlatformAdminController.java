package com.team.ecommerce.admin.controller;

import com.team.ecommerce.admin.dto.AdminDashboardView;
import com.team.ecommerce.admin.dto.AdminLogQuery;
import com.team.ecommerce.admin.dto.AdminLogView;
import com.team.ecommerce.admin.dto.BannerRequest;
import com.team.ecommerce.admin.dto.BannerView;
import com.team.ecommerce.admin.service.PlatformAdminService;
import com.team.ecommerce.common.api.ApiResponse;
import com.team.ecommerce.common.api.PageResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/admin")
public class PlatformAdminController {
    private final PlatformAdminService platformAdminService;
    public PlatformAdminController(PlatformAdminService platformAdminService) { this.platformAdminService = platformAdminService; }

    @GetMapping("/dashboard")
    public ApiResponse<AdminDashboardView> dashboard(@RequestHeader("X-Admin-Id") Long adminId) {
        return ApiResponse.success(platformAdminService.dashboard(adminId));
    }

    @GetMapping("/logs")
    public ApiResponse<PageResult<AdminLogView>> listLogs(@RequestHeader("X-Admin-Id") Long adminId,
            @RequestParam(required = false) String action,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return ApiResponse.success(platformAdminService.listLogs(adminId, new AdminLogQuery(action, page, size)));
    }

    @GetMapping("/banners")
    public ApiResponse<List<BannerView>> listBanners(@RequestHeader("X-Admin-Id") Long adminId) {
        return ApiResponse.success(platformAdminService.listAllBanners(adminId));
    }

    @PostMapping("/banners")
    public ApiResponse<BannerView> createBanner(@RequestHeader("X-Admin-Id") Long adminId,
            @Valid @RequestBody BannerRequest request) {
        return ApiResponse.success(platformAdminService.createBanner(adminId, request));
    }

    @PutMapping("/banners/{id}")
    public ApiResponse<BannerView> updateBanner(@RequestHeader("X-Admin-Id") Long adminId,
            @PathVariable Long id, @Valid @RequestBody BannerRequest request) {
        return ApiResponse.success(platformAdminService.updateBanner(adminId, id, request));
    }

    @DeleteMapping("/banners/{id}")
    public ApiResponse<Void> deleteBanner(@RequestHeader("X-Admin-Id") Long adminId, @PathVariable Long id) {
        platformAdminService.deleteBanner(adminId, id);
        return ApiResponse.success();
    }
}
