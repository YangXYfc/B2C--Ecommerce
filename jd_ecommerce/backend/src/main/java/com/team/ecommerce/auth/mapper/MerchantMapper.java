package com.team.ecommerce.auth.mapper;

import com.team.ecommerce.auth.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商家表 Mapper，SQL 见 src/main/resources/mapper/MerchantMapper.xml。
 */
@Mapper
public interface MerchantMapper {

    Merchant findByUserId(@Param("userId") Long userId);

    /** 新增商家，成功后回填自增主键到 {@code merchant.id}。 */
    int insert(Merchant merchant);
}
