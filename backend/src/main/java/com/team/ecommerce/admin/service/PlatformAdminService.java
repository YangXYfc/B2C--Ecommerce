package com.team.ecommerce.admin.service;

import com.team.ecommerce.admin.dto.AdminDashboardView;
import com.team.ecommerce.admin.dto.AdminLogQuery;
import com.team.ecommerce.admin.dto.AdminLogView;
import com.team.ecommerce.admin.dto.BannerRequest;
import com.team.ecommerce.admin.dto.BannerView;
import com.team.ecommerce.common.api.PageResult;
import java.util.List;

public interface PlatformAdminService {
    AdminDashboardView dashboard(Long adminId);
    PageResult<AdminLogView> listLogs(Long adminId, AdminLogQuery query);
    List<BannerView> listEnabledBanners();
    List<BannerView> listAllBanners(Long adminId);
    BannerView createBanner(Long adminId, BannerRequest request);
    BannerView updateBanner(Long adminId, Long bannerId, BannerRequest request);
    void deleteBanner(Long adminId, Long bannerId);
}
