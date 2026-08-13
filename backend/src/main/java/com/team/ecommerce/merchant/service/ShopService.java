package com.team.ecommerce.merchant.service;

import com.team.ecommerce.auth.entity.Merchant;
import com.team.ecommerce.auth.mapper.MerchantMapper;
import com.team.ecommerce.common.BizException;
import com.team.ecommerce.common.ResultCode;
import com.team.ecommerce.merchant.dto.ShopRequest;
import com.team.ecommerce.merchant.dto.ShopVO;
import com.team.ecommerce.security.UserContext;
import org.springframework.stereotype.Service;

/**
 * 商家店铺资料服务（5.1 / 5.2）。
 */
@Service
public class ShopService {

    private final MerchantMapper merchantMapper;

    public ShopService(MerchantMapper merchantMapper) {
        this.merchantMapper = merchantMapper;
    }

    /** 5.1 店铺资料。 */
    public ShopVO get() {
        return toVO(currentMerchant());
    }

    /** 5.2 更新店铺资料：auditStatus 保持原值。 */
    public ShopVO update(ShopRequest req) {
        Merchant merchant = currentMerchant();
        merchant.setShopName(req.shopName());
        merchant.setShopLogo(req.shopLogo());
        merchant.setDescription(req.description());
        merchant.setContactPhone(req.contactPhone());
        merchantMapper.updateShop(merchant);
        return toVO(merchant);
    }

    /**
     * 当前商家：商家记录存在即可（不校验审核状态，
     * 待审核商家在等待期间也能查看/完善自己的店铺资料，响应中 auditStatus 如实呈现）。
     */
    private Merchant currentMerchant() {
        Merchant merchant = merchantMapper.findByUserId(UserContext.getUserId());
        if (merchant == null) {
            throw new BizException(ResultCode.FORBIDDEN, "非商家或店铺不存在");
        }
        return merchant;
    }

    private ShopVO toVO(Merchant m) {
        return new ShopVO(m.getShopName(), m.getShopLogo(), m.getDescription(),
                m.getContactPhone(), m.getAuditStatus());
    }
}
