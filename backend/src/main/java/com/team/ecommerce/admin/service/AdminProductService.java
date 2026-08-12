package com.team.ecommerce.admin.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.ecommerce.admin.dto.AdminProductDetailVO;
import com.team.ecommerce.admin.dto.AdminProductPendingVO;
import com.team.ecommerce.admin.dto.ProductAuditVO;
import com.team.ecommerce.auth.entity.Merchant;
import com.team.ecommerce.auth.mapper.MerchantMapper;
import com.team.ecommerce.catalog.dto.SkuVO;
import com.team.ecommerce.catalog.entity.Product;
import com.team.ecommerce.catalog.mapper.ProductMapper;
import com.team.ecommerce.catalog.mapper.ProductSkuMapper;
import com.team.ecommerce.common.BizException;
import com.team.ecommerce.common.ResultCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 平台商品审核服务（8.1 / 8.2 / 8.3）。
 */
@Service
public class AdminProductService {

    private static final int STATUS_PENDING = 0;    // 待审核
    private static final int STATUS_ON_SALE = 1;    // 上架（审核通过）
    private static final int STATUS_REJECTED = 3;   // 审核拒绝

    private final ProductMapper productMapper;
    private final ProductSkuMapper productSkuMapper;
    private final MerchantMapper merchantMapper;
    private final ObjectMapper objectMapper;
    private final AdminLogService adminLogService;

    public AdminProductService(ProductMapper productMapper, ProductSkuMapper productSkuMapper,
                               MerchantMapper merchantMapper, ObjectMapper objectMapper,
                               AdminLogService adminLogService) {
        this.productMapper = productMapper;
        this.productSkuMapper = productSkuMapper;
        this.merchantMapper = merchantMapper;
        this.objectMapper = objectMapper;
        this.adminLogService = adminLogService;
    }

    /** 8.1 待审核商品列表（无分页，纯数组）。 */
    public List<AdminProductPendingVO> listPending() {
        return productMapper.findPending().stream().map(this::toPendingVO).toList();
    }

    /** 8.2 待审核商品详情（含 SKU），仅管理员可见。 */
    public AdminProductDetailVO detail(Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new BizException(ResultCode.NOT_FOUND, "商品不存在");
        }
        return new AdminProductDetailVO(
                product.getId(), product.getName(), product.getSubtitle(),
                product.getMainImage(), fromJsonList(product.getSubImages()), product.getDescription(),
                product.getDetailHtml(), product.getPrice(), product.getStatus(), product.getAuditRemark(),
                product.getMerchantId(), shopNameOf(product.getMerchantId()), product.getCategoryId(),
                skusOf(product.getId()));
    }

    /** 8.3 审核商品：通过→status=1（上架）；驳回→status=3（审核拒绝）。 */
    @Transactional
    public ProductAuditVO audit(Long id, Boolean approve, String remark) {
        if (approve == null) {
            throw new BizException(ResultCode.BAD_REQUEST, "approve不能为空");
        }
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new BizException(ResultCode.NOT_FOUND, "商品不存在");
        }
        if (product.getStatus() == null || product.getStatus() != STATUS_PENDING) {
            throw new BizException(ResultCode.BAD_REQUEST, "商品不在待审核状态");
        }
        int status;
        if (approve) {
            productMapper.updateStatus(id, STATUS_ON_SALE, remark);
            status = STATUS_ON_SALE;
        } else {
            productMapper.updateStatus(id, STATUS_REJECTED, remark);
            status = STATUS_REJECTED;
        }

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("action", approve ? "approve" : "reject");
        detail.put("remark", remark);
        adminLogService.record("PRODUCT_AUDIT", "PRODUCT", id, detail);
        return new ProductAuditVO(id, status, remark);
    }

    private AdminProductPendingVO toPendingVO(Product p) {
        return new AdminProductPendingVO(
                p.getId(), p.getName(), p.getMainImage(), p.getPrice(), p.getStatus(),
                p.getMerchantId(), shopNameOf(p.getMerchantId()), p.getCategoryId(), p.getCreatedAt());
    }

    private String shopNameOf(Long merchantId) {
        Merchant merchant = merchantMapper.findById(merchantId);
        return merchant == null ? null : merchant.getShopName();
    }

    private List<SkuVO> skusOf(Long productId) {
        return productSkuMapper.findByProductId(productId).stream()
                .map(s -> new SkuVO(s.getId(), s.getSkuName(), s.getPrice(), s.getOriginalPrice(),
                        s.getStock(), fromJsonMap(s.getAttributes()), s.getSkuImage()))
                .toList();
    }

    private List<String> fromJsonList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            return List.of();
        }
    }

    private Map<String, String> fromJsonMap(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            return Map.of();
        }
    }
}
