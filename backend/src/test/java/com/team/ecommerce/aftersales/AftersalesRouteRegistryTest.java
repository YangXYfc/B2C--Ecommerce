package com.team.ecommerce.aftersales;

import static com.team.ecommerce.support.RouteRegistryAssertions.assertRoute;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootTest
class AftersalesRouteRegistryTest {
    @Autowired private RequestMappingHandlerMapping mappings;

    @Test
    void registersDocumentedReviewAndRefundRoutes() {
        assertRoute(mappings, HttpMethod.POST, "/api/reviews");
        assertRoute(mappings, HttpMethod.GET, "/api/products/{id}/reviews");
        assertRoute(mappings, HttpMethod.GET, "/api/merchant/reviews");
        assertRoute(mappings, HttpMethod.PUT, "/api/merchant/reviews/{id}/reply");
        assertRoute(mappings, HttpMethod.POST, "/api/refunds");
        assertRoute(mappings, HttpMethod.GET, "/api/refunds");
        assertRoute(mappings, HttpMethod.GET, "/api/refunds/{id}");
        assertRoute(mappings, HttpMethod.PUT, "/api/refunds/{id}/return-logistics");
        assertRoute(mappings, HttpMethod.PUT, "/api/refunds/{id}/appeal");
        assertRoute(mappings, HttpMethod.GET, "/api/merchant/refunds");
        assertRoute(mappings, HttpMethod.PUT, "/api/merchant/refunds/{id}/audit");
        assertRoute(mappings, HttpMethod.PUT, "/api/merchant/refunds/{id}/confirm-return");
        assertRoute(mappings, HttpMethod.GET, "/api/admin/refunds");
        assertRoute(mappings, HttpMethod.PUT, "/api/admin/refunds/{id}/arbitrate");
    }
}
