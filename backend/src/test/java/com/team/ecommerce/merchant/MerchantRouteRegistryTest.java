package com.team.ecommerce.merchant;

import static com.team.ecommerce.support.RouteRegistryAssertions.assertRoute;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootTest
class MerchantRouteRegistryTest {
    @Autowired private RequestMappingHandlerMapping mappings;

    @Test
    void registersMerchantTradeShopAndProductRoutes() {
        assertRoute(mappings, HttpMethod.GET, "/api/merchant/dashboard");
        assertRoute(mappings, HttpMethod.GET, "/api/merchant/orders");
        assertRoute(mappings, HttpMethod.GET, "/api/merchant/orders/{id}");
        assertRoute(mappings, HttpMethod.PUT, "/api/merchant/orders/{id}/ship");
        assertRoute(mappings, HttpMethod.GET, "/api/merchant/shop");
        assertRoute(mappings, HttpMethod.POST, "/api/merchant/products");
    }
}
