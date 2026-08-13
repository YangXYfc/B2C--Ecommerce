package com.team.ecommerce.aftersales.review.mapper;

import com.team.ecommerce.aftersales.review.entity.ReviewEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewMapper {
    ReviewEntity selectById(Long id);
    List<ReviewEntity> selectByProductId(@Param("productId") Long productId,
            @Param("offset") int offset, @Param("size") int size);
    List<ReviewEntity> selectByMerchantId(@Param("merchantId") Long merchantId,
            @Param("offset") int offset, @Param("size") int size);
    long countByProductId(Long productId);
    long countByMerchantId(Long merchantId);
    int insert(ReviewEntity entity);
    int updateById(ReviewEntity entity);
    long lastInsertId();
}
