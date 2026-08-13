package com.team.ecommerce.merchant;

import static com.team.ecommerce.support.RouteRegistryAssertions.assertNoRoute;
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
    void registersOnlyDocumentedERoleMerchantTradeRoutes() {
        assertRoute(mappings, HttpMethod.GET, "/api/merchant/dashboard");
        assertRoute(mappings, HttpMethod.GET, "/api/merchant/orders");
        assertRoute(mappings, HttpMethod.GET, "/api/merchant/orders/{id}");
        assertRoute(mappings, HttpMethod.PUT, "/api/merchant/orders/{id}/ship");
        assertNoRoute(mappings, HttpMethod.GET, "/api/merchant/shop");
        assertNoRoute(mappings, HttpMethod.POST, "/api/merchant/products");
    }
}
