package com.team.ecommerce.trade.payment.mapper;

import com.team.ecommerce.trade.payment.entity.PaymentEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper {
    PaymentEntity selectById(Long id);
    PaymentEntity selectByOrderId(Long orderId);
    int insert(PaymentEntity entity);
    int updateById(PaymentEntity entity);
    long lastInsertId();
}
