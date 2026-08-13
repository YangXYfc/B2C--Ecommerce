package com.team.ecommerce.admin;

import static com.team.ecommerce.support.RouteRegistryAssertions.assertNoRoute;
import static com.team.ecommerce.support.RouteRegistryAssertions.assertRoute;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootTest
class AdminRouteRegistryTest {
    @Autowired private RequestMappingHandlerMapping mappings;

    @Test
    void registersOnlyDocumentedERolePlatformRoutes() {
        assertRoute(mappings, HttpMethod.GET, "/api/admin/dashboard");
        assertRoute(mappings, HttpMethod.GET, "/api/admin/logs");
        assertRoute(mappings, HttpMethod.GET, "/api/banners");
        assertRoute(mappings, HttpMethod.GET, "/api/admin/banners");
        assertRoute(mappings, HttpMethod.POST, "/api/admin/banners");
        assertRoute(mappings, HttpMethod.PUT, "/api/admin/banners/{id}");
        assertRoute(mappings, HttpMethod.DELETE, "/api/admin/banners/{id}");
        assertNoRoute(mappings, HttpMethod.GET, "/api/admin/users");
        assertNoRoute(mappings, HttpMethod.GET, "/api/admin/merchants/pending");
        assertNoRoute(mappings, HttpMethod.GET, "/api/admin/products/pending");
    }
}
