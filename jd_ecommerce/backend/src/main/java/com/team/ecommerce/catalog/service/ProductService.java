package com.team.ecommerce.catalog.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.ecommerce.auth.entity.Merchant;
import com.team.ecommerce.auth.mapper.MerchantMapper;
import com.team.ecommerce.catalog.dto.MerchantProductDetailVO;
import com.team.ecommerce.catalog.dto.MerchantProductListVO;
import com.team.ecommerce.catalog.dto.ProductDetailVO;
import com.team.ecommerce.catalog.dto.ProductIdVO;
import com.team.ecommerce.catalog.dto.ProductListVO;
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
import com.team.ecommerce.common.BizException;
import com.team.ecommerce.common.PageResult;
import com.team.ecommerce.common.ResultCode;
import com.team.ecommerce.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品/规格/库存服务（4.1–4.9）。
 */
@Service
public class ProductService {

    /** 商品状态：0-待审核 1-上架 2-下架 3-审核拒绝。 */
    private static final int STATUS_PENDING = 0;
    private static final int STATUS_ON_SALE = 1;
    private static final int STATUS_OFF_SHELF = 2;
    /** 商家审核状态：1-审核通过。 */
    private static final int MERCHANT_AUDIT_PASSED = 1;
    /** 分类状态：1-显示。 */
    private static final int STATUS_SHOW = 1;
    /** 分页默认值。 */
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 100;

    private final ProductMapper productMapper;
    private final ProductSkuMapper productSkuMapper;
    private final CategoryMapper categoryMapper;
    private final MerchantMapper merchantMapper;
    private final ObjectMapper objectMapper;

    public ProductService(ProductMapper productMapper, ProductSkuMapper productSkuMapper,
                          CategoryMapper categoryMapper, MerchantMapper merchantMapper,
                          ObjectMapper objectMapper) {
        this.productMapper = productMapper;
        this.productSkuMapper = productSkuMapper;
        this.categoryMapper = categoryMapper;
        this.merchantMapper = merchantMapper;
        this.objectMapper = objectMapper;
    }

    // ---------- 4.1 公开商品列表 ----------

    /**
     * 4.1 商品列表：仅 status=1，keyword 模糊匹配名称，
     * categoryId 含自身及子分类，sort 排序，分页。
     */
    public PageResult<ProductListVO> list(String keyword, Long categoryId, String sort, Integer page, Integer size) {
        int p = page == null || page < 1 ? 1 : page;
        int s = sizeOf(size);
        List<Long> categoryIds = null;
        if (categoryId != null) {
            categoryIds = descendantCategoryIds(categoryId);
            if (categoryIds.isEmpty()) {
                return new PageResult<>(0, p, s, List.of());
            }
        }

        long total = productMapper.countPublic(STATUS_ON_SALE, keyword, categoryIds);
        List<ProductListVO> list = productMapper
                .findPublicPage(STATUS_ON_SALE, keyword, categoryIds, sort, (p - 1) * s, s)
                .stream().map(this::toProductListVO).toList();
        return new PageResult<>(total, p, s, list);
    }

    /** 4.2 商品详情：仅 status=1 可见，附商家名、分类名、全部可用 SKU。 */
    public ProductDetailVO detail(Long id) {
        Product product = productMapper.findById(id);
        if (product == null || product.getStatus() == null || product.getStatus() != STATUS_ON_SALE) {
            throw new BizException(ResultCode.NOT_FOUND, "商品不存在或未上架");
        }

        String merchantName = null;
        Merchant merchant = merchantMapper.findById(product.getMerchantId());
        if (merchant != null) {
            merchantName = merchant.getShopName();
        }
        String categoryName = null;
        Category category = categoryMapper.findById(product.getCategoryId());
        if (category != null) {
            categoryName = category.getName();
        }

        return new ProductDetailVO(product.getId(), product.getName(), product.getSubtitle(),
                product.getMainImage(), fromJsonList(product.getSubImages()), product.getDescription(),
                product.getDetailHtml(), product.getPrice(), product.getSalesCount(),
                product.getMerchantId(), merchantName, product.getCategoryId(), categoryName,
                skusOf(product.getId()));
    }

    // ---------- 4.4 / 4.5 商家商品 ----------

    /** 4.4 商家商品列表：可选 status 过滤，创建时间倒序。 */
    public PageResult<MerchantProductListVO> merchantList(Integer status, Integer page, Integer size) {
        Merchant merchant = currentMerchant();
        int p = page == null || page < 1 ? 1 : page;
        int s = sizeOf(size);

        long total = productMapper.countMerchant(merchant.getId(), status);
        List<MerchantProductListVO> list = productMapper
                .findMerchantPage(merchant.getId(), status, (p - 1) * s, s)
                .stream().map(this::toMerchantProductListVO).toList();
        return new PageResult<>(total, p, s, list);
    }

    /** 4.5 商家商品详情：含审核信息，供编辑页回填；非本人 → 403。 */
    public MerchantProductDetailVO merchantDetail(Long id) {
        Product product = requireOwnedProduct(id);
        return new MerchantProductDetailVO(product.getId(), product.getName(), product.getSubtitle(),
                product.getMainImage(), fromJsonList(product.getSubImages()), product.getDescription(),
                product.getDetailHtml(), product.getPrice(), product.getStatus(), product.getAuditRemark(),
                product.getSalesCount(), product.getCategoryId(), skusOf(product.getId()));
    }

    // ---------- 4.6 / 4.7 / 4.8 商品增改、下架 ----------

    /** 4.6 发布商品：写 status=0，price 取 SKU 最低价，返回 productId。 */
    @Transactional
    public ProductIdVO create(ProductRequest req) {
        Merchant merchant = currentMerchant();
        requireCategory(req.categoryId());
        if (req.skus() == null || req.skus().isEmpty()) {
            throw new BizException(ResultCode.BAD_REQUEST, "至少需要一个SKU");
        }

        Product product = new Product();
        fillProduct(product, req);
        product.setMerchantId(merchant.getId());
        product.setPrice(minPrice(req.skus()));
        product.setStatus(STATUS_PENDING);
        product.setAuditRemark(null);
        product.setSalesCount(0);
        productMapper.insert(product);

        insertSkus(product.getId(), req.skus());
        return new ProductIdVO(product.getId());
    }

    /** 4.7 编辑商品：整体替换 SKU，重算 price，status 重置为待审核。 */
    @Transactional
    public ProductStatusVO update(Long id, ProductRequest req) {
        requireOwnedProduct(id);
        requireCategory(req.categoryId());
        if (req.skus() == null || req.skus().isEmpty()) {
            throw new BizException(ResultCode.BAD_REQUEST, "至少需要一个SKU");
        }

        Product product = productMapper.findById(id);
        fillProduct(product, req);
        product.setPrice(minPrice(req.skus()));
        product.setStatus(STATUS_PENDING);
        product.setAuditRemark(null);
        productMapper.update(product);

        // 有订单引用的 SKU 不能物理删除（order_item 外键 RESTRICT）：未卖过的删除，卖过的保留但停用，
        // 新清单全部新增。详情查询只取 status=1，停用后各端均不可见，order_item 快照继续有效。
        productSkuMapper.deleteUnreferenced(id);
        productSkuMapper.disableReferenced(id);
        insertSkus(id, req.skus());
        return new ProductStatusVO(id, STATUS_PENDING);
    }

    /** 4.8 下架：status → 2，消费者端不再展示。 */
    public ProductStatusVO offShelf(Long id) {
        requireOwnedProduct(id);
        productMapper.updateStatus(id, STATUS_OFF_SHELF, null);
        return new ProductStatusVO(id, STATUS_OFF_SHELF);
    }

    // ---------- 4.9 SKU 库存 ----------

    /** 4.9 修改 SKU 库存（绝对值，不允许负数）；非本人商品 SKU → 403。 */
    public StockVO updateStock(Long skuId, Integer stock) {
        if (stock == null || stock < 0) {
            throw new BizException(ResultCode.BAD_REQUEST, "库存不能为负数");
        }
        Merchant merchant = currentMerchant();
        ProductSku sku = productSkuMapper.findById(skuId);
        if (sku == null) {
            throw new BizException(ResultCode.NOT_FOUND, "SKU不存在");
        }
        Product product = productMapper.findById(sku.getProductId());
        if (product == null || !merchant.getId().equals(product.getMerchantId())) {
            throw new BizException(ResultCode.FORBIDDEN, "只能修改自己商品的SKU");
        }
        productSkuMapper.updateStock(skuId, stock);
        return new StockVO(skuId, stock);
    }

    // ---------- 私有辅助 ----------

    /** 当前商家：账号须通过审核，否则 403（所有 /api/merchant/** 前置门槛）。 */
    private Merchant currentMerchant() {
        Merchant merchant = merchantMapper.findByUserId(UserContext.getUserId());
        if (merchant == null || merchant.getAuditStatus() == null
                || merchant.getAuditStatus() != MERCHANT_AUDIT_PASSED) {
            throw new BizException(ResultCode.FORBIDDEN, "商家未通过审核");
        }
        return merchant;
    }

    /** 查询并校验商品归属：不存在 → 404，非本人 → 403。 */
    private Product requireOwnedProduct(Long id) {
        Merchant merchant = currentMerchant();
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new BizException(ResultCode.NOT_FOUND, "商品不存在");
        }
        if (!merchant.getId().equals(product.getMerchantId())) {
            throw new BizException(ResultCode.FORBIDDEN, "只能操作自己的商品");
        }
        return product;
    }

    /** 分类必须存在，否则 400。 */
    private void requireCategory(Long categoryId) {
        if (categoryId == null || categoryMapper.findById(categoryId) == null) {
            throw new BizException(ResultCode.BAD_REQUEST, "商品分类不存在");
        }
    }

    /** categoryId 自身 + 全部后代分类 id（基于启用的分类树）。 */
    private List<Long> descendantCategoryIds(Long categoryId) {
        Map<Long, List<Category>> byParent = categoryMapper.findByStatus(STATUS_SHOW).stream()
                .collect(Collectors.groupingBy(Category::getParentId));
        List<Long> ids = new ArrayList<>();
        collectDescendants(categoryId, byParent, ids);
        return ids;
    }

    private void collectDescendants(Long parentId, Map<Long, List<Category>> byParent, List<Long> acc) {
        acc.add(parentId);
        for (Category c : byParent.getOrDefault(parentId, List.of())) {
            collectDescendants(c.getId(), byParent, acc);
        }
    }

    /** 填充商品基本信息（不含 merchantId/salesCount/status/auditRemark/price）。 */
    private void fillProduct(Product product, ProductRequest req) {
        product.setCategoryId(req.categoryId());
        product.setName(req.name());
        product.setSubtitle(req.subtitle());
        product.setMainImage(req.mainImage());
        product.setSubImages(toJson(req.subImages()));
        product.setDescription(req.description());
        product.setDetailHtml(req.detailHtml());
    }

    /** 插入该商品的全部 SKU，attributes 转 JSON 字符串。 */
    private void insertSkus(Long productId, List<SkuRequest> skuRequests) {
        for (SkuRequest r : skuRequests) {
            ProductSku sku = new ProductSku();
            sku.setProductId(productId);
            sku.setSkuName(r.skuName());
            sku.setPrice(r.price());
            sku.setOriginalPrice(r.originalPrice());
            sku.setStock(r.stock());
            sku.setAttributes(toJson(r.attributes()));
            sku.setSkuImage(r.skuImage());
            sku.setStatus(1);
            productSkuMapper.insert(sku);
        }
    }

    /** 取 SKU 最低售价作为商品展示价。 */
    private BigDecimal minPrice(List<SkuRequest> skus) {
        return skus.stream().map(SkuRequest::price).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
    }

    /** 某商品启用 SKU 的 VO 列表（attributes 反序列化为 Map）。 */
    private List<SkuVO> skusOf(Long productId) {
        return productSkuMapper.findByProductId(productId).stream()
                .map(s -> new SkuVO(s.getId(), s.getSkuName(), s.getPrice(), s.getOriginalPrice(),
                        s.getStock(), fromJsonMap(s.getAttributes()), s.getSkuImage()))
                .toList();
    }

    private ProductListVO toProductListVO(Product p) {
        return new ProductListVO(p.getId(), p.getName(), p.getSubtitle(),
                p.getMainImage(), p.getPrice(), p.getSalesCount());
    }

    private MerchantProductListVO toMerchantProductListVO(Product p) {
        return new MerchantProductListVO(p.getId(), p.getName(), p.getMainImage(), p.getPrice(),
                p.getStatus(), p.getAuditRemark(), p.getSalesCount(), p.getCreatedAt());
    }

    /** size 校验：默认 10，最大 100。 */
    private int sizeOf(Integer size) {
        if (size == null || size < 1) {
            return DEFAULT_SIZE;
        }
        if (size > MAX_SIZE) {
            throw new BizException(ResultCode.BAD_REQUEST, "size 不能超过100");
        }
        return size;
    }

    // ---------- JSON 转换（sub_images / attributes 存 JSON 字符串） ----------

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new BizException(ResultCode.INTERNAL_SERVER_ERROR, "JSON 序列化失败");
        }
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
