package com.team.ecommerce.trade;

import static com.team.ecommerce.support.RouteRegistryAssertions.assertRoute;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootTest
class TradeRouteRegistryTest {

    @Autowired
    private RequestMappingHandlerMapping mappings;

    @Test
    void registersDocumentedCartAndOrderRoutes() {
        assertRoute(mappings, HttpMethod.GET, "/api/cart");
        assertRoute(mappings, HttpMethod.POST, "/api/cart/items");
        assertRoute(mappings, HttpMethod.PUT, "/api/cart/items/{id}");
        assertRoute(mappings, HttpMethod.DELETE, "/api/cart/items/{id}");
        assertRoute(mappings, HttpMethod.DELETE, "/api/cart/selected");
        assertRoute(mappings, HttpMethod.POST, "/api/orders");
        assertRoute(mappings, HttpMethod.GET, "/api/orders");
        assertRoute(mappings, HttpMethod.GET, "/api/orders/{id}");
        assertRoute(mappings, HttpMethod.PUT, "/api/orders/{id}/cancel");
        assertRoute(mappings, HttpMethod.POST, "/api/orders/{id}/pay");
        assertRoute(mappings, HttpMethod.PUT, "/api/orders/{id}/confirm-receipt");
    }
}
