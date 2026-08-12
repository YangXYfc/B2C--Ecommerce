package com.team.ecommerce.merchant.controller;

import com.team.ecommerce.common.Result;
import com.team.ecommerce.merchant.dto.ShopRequest;
import com.team.ecommerce.merchant.dto.ShopVO;
import com.team.ecommerce.merchant.service.ShopService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家店铺接口（5.1 / 5.2），由 JwtAuthInterceptor 按前缀限制 MERCHANT 角色。
 */
@RestController
@RequestMapping("/api/merchant/shop")
public class MerchantShopController {

    private final ShopService shopService;

    public MerchantShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    /** 5.1 店铺资料。 */
    @GetMapping
    public Result<ShopVO> get() {
        return Result.success(shopService.get());
    }

    /** 5.2 更新店铺资料。 */
    @PutMapping
    public Result<ShopVO> update(@Valid @RequestBody ShopRequest req) {
        return Result.success(shopService.update(req), "更新成功");
    }
}
