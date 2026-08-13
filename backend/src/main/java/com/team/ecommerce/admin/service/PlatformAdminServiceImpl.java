package com.team.ecommerce.admin.service;

import com.team.ecommerce.admin.dto.AdminDashboardView;
import com.team.ecommerce.admin.dto.AdminLogQuery;
import com.team.ecommerce.admin.dto.AdminLogView;
import com.team.ecommerce.admin.dto.BannerRequest;
import com.team.ecommerce.admin.dto.BannerView;
import com.team.ecommerce.admin.entity.AdminLogEntity;
import com.team.ecommerce.admin.entity.BannerEntity;
import com.team.ecommerce.admin.mapper.AdminLogMapper;
import com.team.ecommerce.admin.mapper.BannerMapper;
import com.team.ecommerce.common.api.PageResult;
import com.team.ecommerce.common.error.BusinessException;
import com.team.ecommerce.common.error.ErrorCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlatformAdminServiceImpl implements PlatformAdminService {

    private final BannerMapper bannerMapper;
    private final AdminLogMapper adminLogMapper;

    // For dashboard stats we'd normally have dedicated COUNT queries.
    // In this implementation we approximate via Mapper queries.
    private final AdminDashboardMapper adminDashboardMapper;

    public PlatformAdminServiceImpl(BannerMapper bannerMapper, AdminLogMapper adminLogMapper,
            AdminDashboardMapper adminDashboardMapper) {
        this.bannerMapper = bannerMapper;
        this.adminLogMapper = adminLogMapper;
        this.adminDashboardMapper = adminDashboardMapper;
    }

    @Override
    public AdminDashboardView dashboard(Long adminId) {
        long userCount = adminDashboardMapper.countUsers();
        long merchantCount = adminDashboardMapper.countMerchants();
        long orderCount = adminDashboardMapper.countOrders();
        BigDecimal salesAmount = adminDashboardMapper.sumSales();
        return new AdminDashboardView(userCount, merchantCount, orderCount,
                salesAmount != null ? salesAmount : BigDecimal.ZERO);
    }

    @Override
    public PageResult<AdminLogView> listLogs(Long adminId, AdminLogQuery query) {
        int offset = (query.page() - 1) * query.size();
        var logs = adminLogMapper.selectPage(query.action(), offset, query.size());
        var views = logs.stream().map(l -> new AdminLogView(l.id(), l.adminId(), l.action(),
                l.targetType(), l.targetId(), l.detail(), l.ipAddress(), l.createdAt())).toList();
        long total = adminLogMapper.countByAction(query.action());
        return new PageResult<>(views, total, query.page(), query.size());
    }

    @Override
    public List<BannerView> listEnabledBanners() {
        return bannerMapper.selectEnabled().stream()
                .map(PlatformAdminServiceImpl::toView).toList();
    }

    @Override
    public List<BannerView> listAllBanners(Long adminId) {
        return bannerMapper.selectAll().stream()
                .map(PlatformAdminServiceImpl::toView).toList();
    }

    @Override
    @Transactional
    public BannerView createBanner(Long adminId, BannerRequest request) {
        var entity = new BannerEntity(null, request.title(), request.imageUrl(),
                request.linkUrl(), request.sort(),
                request.enabled() ? 1 : 0, null, null);
        bannerMapper.insert(entity);
        var inserted = new BannerEntity(bannerMapper.lastInsertId(), entity.title(),
                entity.imageUrl(), entity.linkUrl(), entity.sort(), entity.status(),
                entity.createdAt(), entity.updatedAt());

        // Log operation
        var log = new AdminLogEntity(null, adminId, "BANNER_CREATE", "BANNER", inserted.id(),
                "{\"title\":\"" + request.title() + "\"}", "127.0.0.1", null);
        adminLogMapper.insert(log);

        return toView(inserted);
    }

    @Override
    @Transactional
    public BannerView updateBanner(Long adminId, Long bannerId, BannerRequest request) {
        var existing = bannerMapper.selectById(bannerId);
        if (existing == null) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "轮播图不存在");
        }
        var entity = new BannerEntity(bannerId, request.title(), request.imageUrl(),
                request.linkUrl(), request.sort(),
                request.enabled() ? 1 : 0, existing.createdAt(), existing.updatedAt());
        bannerMapper.updateById(entity);

        var log = new AdminLogEntity(null, adminId, "BANNER_UPDATE", "BANNER", bannerId,
                "{\"title\":\"" + request.title() + "\"}", "127.0.0.1", null);
        adminLogMapper.insert(log);

        return toView(entity);
    }

    @Override
    @Transactional
    public void deleteBanner(Long adminId, Long bannerId) {
        var existing = bannerMapper.selectById(bannerId);
        if (existing == null) {
            throw new BusinessException(ErrorCode.INVALID_ARGUMENT, "轮播图不存在");
        }
        bannerMapper.deleteById(bannerId);

        var log = new AdminLogEntity(null, adminId, "BANNER_DELETE", "BANNER", bannerId,
                "{\"title\":\"" + existing.title() + "\"}", "127.0.0.1", null);
        adminLogMapper.insert(log);
    }

    private static BannerView toView(BannerEntity e) {
        return new BannerView(e.id(), e.title(), e.imageUrl(), e.linkUrl(),
                e.sort(), e.status() == 1, e.createdAt(), e.updatedAt());
    }
}
