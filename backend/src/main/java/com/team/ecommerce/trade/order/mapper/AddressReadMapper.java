package com.team.ecommerce.trade.order.mapper;

import org.apache.ibatis.annotations.Mapper;

/** E-role read-only access to address for order creation snapshot. */
@Mapper
public interface AddressReadMapper {

    AddressInfo selectById(Long id);

    record AddressInfo(String name, String phone, String province, String city,
            String district, String detail) {}
}
