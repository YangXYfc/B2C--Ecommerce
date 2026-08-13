package com.team.ecommerce.admin.mapper;

import com.team.ecommerce.admin.entity.AdminLogEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminLogMapper {
    AdminLogEntity selectById(Long id);
    List<AdminLogEntity> selectPage(@Param("action") String action,
            @Param("offset") int offset, @Param("size") int size);
    long countByAction(@Param("action") String action);
    int insert(AdminLogEntity entity);
}
