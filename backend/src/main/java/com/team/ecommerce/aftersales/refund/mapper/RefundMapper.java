package com.team.ecommerce.aftersales.refund.mapper;

import com.team.ecommerce.aftersales.refund.entity.RefundEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RefundMapper {
    RefundEntity selectById(Long id);
    List<RefundEntity> selectByUser(@Param("userId") Long userId, @Param("status") Integer status,
            @Param("offset") int offset, @Param("size") int size);
    List<RefundEntity> selectByMerchant(@Param("merchantId") Long merchantId, @Param("status") Integer status,
            @Param("offset") int offset, @Param("size") int size);
    List<RefundEntity> selectForArbitration(@Param("offset") int offset, @Param("size") int size);
    int insert(RefundEntity entity);
    int updateById(RefundEntity entity);
}
