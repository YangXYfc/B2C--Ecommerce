package com.team.ecommerce.auth.mapper;

import com.team.ecommerce.auth.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /** 用户列表一页（6.1，不查密码列），keyword 模糊搜用户名/昵称/手机号，可选 role/status 过滤。 */
    List<User> findPage(@Param("keyword") String keyword, @Param("role") String role,
                        @Param("status") Integer status, @Param("offset") int offset, @Param("size") int size);

    /** 用户列表总数（条件与 findPage 一致）。 */
    long count(@Param("keyword") String keyword, @Param("role") String role,
               @Param("status") Integer status);

    /** 禁用/启用用户（6.2）。 */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /** 更新用户角色（7.3 审核通过时改为 MERCHANT）。 */
    int updateRole(@Param("id") Long id, @Param("role") String role);
}
