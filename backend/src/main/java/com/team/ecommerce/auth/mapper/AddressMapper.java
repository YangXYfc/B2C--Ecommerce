package com.team.ecommerce.auth.mapper;

import com.team.ecommerce.auth.entity.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 收货地址表 Mapper，SQL 见 src/main/resources/mapper/AddressMapper.xml。
 */
@Mapper
public interface AddressMapper {

    List<Address> findByUserId(@Param("userId") Long userId);

    Address findById(@Param("id") Long id);

    /** 新增地址，成功后回填自增主键到 {@code address.id}。 */
    int insert(Address address);

    /** 更新地址资料（不含 user_id）。 */
    int update(Address address);

    /** 把该用户全部地址置为非默认。 */
    int clearDefault(@Param("userId") Long userId);

    /** 把指定地址设为默认。 */
    int setDefault(@Param("id") Long id);

    int deleteById(@Param("id") Long id);
}
