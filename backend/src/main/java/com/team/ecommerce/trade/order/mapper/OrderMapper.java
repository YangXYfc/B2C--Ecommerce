package com.team.ecommerce.trade.order.mapper;

import com.team.ecommerce.trade.order.entity.OrderEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderMapper {
    OrderEntity selectById(Long id);
    List<OrderEntity> selectByUser(@Param("userId") Long userId, @Param("status") Integer status,
            @Param("offset") int offset, @Param("size") int size);
    List<OrderEntity> selectByMerchant(@Param("merchantId") Long merchantId, @Param("status") Integer status,
            @Param("offset") int offset, @Param("size") int size);
    long countByUser(@Param("userId") Long userId, @Param("status") Integer status);
    long countByMerchant(@Param("merchantId") Long merchantId, @Param("status") Integer status);
    int insert(OrderEntity entity);
    int updateById(OrderEntity entity);
    long lastInsertId();
}
