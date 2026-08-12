package com.team.ecommerce.admin.mapper;

import com.team.ecommerce.admin.entity.AdminLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员操作日志 Mapper，SQL 见 src/main/resources/mapper/AdminLogMapper.xml。
 */
@Mapper
public interface AdminLogMapper {

    /** 新增一条操作日志，成功后回填自增主键到 {@code adminLog.id}。 */
    int insert(AdminLog adminLog);
}
