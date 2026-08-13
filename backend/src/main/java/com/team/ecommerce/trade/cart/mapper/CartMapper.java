package com.team.ecommerce.trade.cart.mapper;

import com.team.ecommerce.trade.cart.dto.CartItemView;
import com.team.ecommerce.trade.cart.entity.CartEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CartMapper {
    CartEntity selectById(Long id);
    List<CartEntity> selectByUserId(Long userId);

    /** Returns cart items joined with product_sku and product for display. */
    List<CartItemView> selectCartItemViews(Long userId);

    int insert(CartEntity entity);
    int updateById(CartEntity entity);
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);
    int deleteSelectedByUserId(Long userId);
}
