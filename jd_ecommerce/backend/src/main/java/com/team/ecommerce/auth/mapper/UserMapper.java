package com.team.ecommerce.auth.mapper;

import com.team.ecommerce.auth.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户表 Mapper，SQL 见 src/main/resources/mapper/UserMapper.xml。
 */
@Mapper
public interface UserMapper {

    User findByUsername(@Param("username") String username);

    User findByPhone(@Param("phone") String phone);

    User findById(@Param("id") Long id);

    /** 新增用户，成功后回填自增主键到 {@code user.id}。 */
    int insert(User user);

    /** 动态更新昵称/头像/手机号。 */
    int updateProfile(User user);

    int updatePassword(@Param("id") Long id, @Param("password") String password);
}
