package com.team.ecommerce.catalog.mapper;

import com.team.ecommerce.catalog.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品 Mapper。
 */
@Mapper
public interface ProductMapper {

    /** 公开列表一页（仅 status=1，支持 keyword/categoryIds/sort）。 */
    List<Product> findPublicPage(@Param("status") Integer status,
                                 @Param("keyword") String keyword,
                                 @Param("categoryIds") List<Long> categoryIds,
                                 @Param("sort") String sort,
                                 @Param("offset") int offset,
                                 @Param("size") int size);

    /** 公开列表总数（条件与 findPublicPage 一致）。 */
    long countPublic(@Param("status") Integer status,
                     @Param("keyword") String keyword,
                     @Param("categoryIds") List<Long> categoryIds);

    /** 商家商品列表一页（按商家 + 可选状态过滤，创建时间倒序）。 */
    List<Product> findMerchantPage(@Param("merchantId") Long merchantId,
                                   @Param("status") Integer status,
                                   @Param("offset") int offset,
                                   @Param("size") int size);

    /** 商家商品列表总数。 */
    long countMerchant(@Param("merchantId") Long merchantId,
                       @Param("status") Integer status);

    Product findById(@Param("id") Long id);

    /** 待审核商品列表（status=0，创建时间倒序，供平台审核 8.1）。 */
    List<Product> findPending();

    /** 新增商品，成功后回填自增主键到 {@code product.id}。 */
    int insert(Product product);

    /** 更新商品基本信息（含状态、审核备注），不更新 merchantId/salesCount。 */
    int update(Product product);

    /** 仅更新状态与审核备注（如下架）。 */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("auditRemark") String auditRemark);
}
