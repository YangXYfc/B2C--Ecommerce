package com.team.ecommerce.auth.mapper;

import com.team.ecommerce.auth.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商家表 Mapper，SQL 见 src/main/resources/mapper/MerchantMapper.xml。
 */
@Mapper
public interface MerchantMapper {

    Merchant findByUserId(@Param("userId") Long userId);

    Merchant findById(@Param("id") Long id);

    /** 新增商家，成功后回填自增主键到 {@code merchant.id}。 */
    int insert(Merchant merchant);

    /** 更新店铺资料（shopName/shopLogo/description/contactPhone），不改审核状态。 */
    int updateShop(Merchant merchant);

    /** 待审核商家列表（7.1）：audit_status = 0，按 id 升序。 */
    List<Merchant> findPending();

    /** 审核商家（7.3）：更新审核状态/营业状态/审核备注。 */
    int updateAudit(@Param("id") Long id,
                    @Param("auditStatus") Integer auditStatus,
                    @Param("status") Integer status,
                    @Param("auditRemark") String auditRemark);
}
