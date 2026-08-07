package com.team.ecommerce.trade.order.mapper;

import com.team.ecommerce.trade.order.entity.OrderItemEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper {
    OrderItemEntity selectById(Long id);
    List<OrderItemEntity> selectByOrderId(Long orderId);
    int insert(OrderItemEntity entity);
}
