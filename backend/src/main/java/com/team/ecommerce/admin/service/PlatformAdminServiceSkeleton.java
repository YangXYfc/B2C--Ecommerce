package com.team.ecommerce.admin.service;

import com.team.ecommerce.admin.dto.AdminDashboardView;
import com.team.ecommerce.admin.dto.AdminLogQuery;
import com.team.ecommerce.admin.dto.AdminLogView;
import com.team.ecommerce.admin.dto.BannerRequest;
import com.team.ecommerce.admin.dto.BannerView;
import com.team.ecommerce.common.api.PageResult;
import com.team.ecommerce.common.error.FeatureNotImplementedException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PlatformAdminServiceSkeleton implements PlatformAdminService {
    public AdminDashboardView dashboard(Long adminId) { throw pending("admin.dashboard"); }
    public PageResult<AdminLogView> listLogs(Long adminId, AdminLogQuery query) { throw pending("admin.logs.list"); }
    public List<BannerView> listEnabledBanners() { throw pending("banner.listEnabled"); }
    public List<BannerView> listAllBanners(Long adminId) { throw pending("admin.banners.list"); }
    public BannerView createBanner(Long adminId, BannerRequest request) { throw pending("admin.banners.create"); }
    public BannerView updateBanner(Long adminId, Long bannerId, BannerRequest request) { throw pending("admin.banners.update"); }
    public void deleteBanner(Long adminId, Long bannerId) { throw pending("admin.banners.delete"); }
    private FeatureNotImplementedException pending(String operation) { return new FeatureNotImplementedException(operation); }
}
