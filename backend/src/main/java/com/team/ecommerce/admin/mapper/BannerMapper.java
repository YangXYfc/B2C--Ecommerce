package com.team.ecommerce.admin.mapper;

import com.team.ecommerce.admin.entity.BannerEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BannerMapper {
    BannerEntity selectById(Long id);
    List<BannerEntity> selectEnabled();
    List<BannerEntity> selectAll();
    int insert(BannerEntity entity);
    int updateById(BannerEntity entity);
    int deleteById(Long id);
    long lastInsertId();
}
