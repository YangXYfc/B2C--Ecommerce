package com.team.ecommerce.support;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public final class RouteRegistryAssertions {

    private RouteRegistryAssertions() {
    }

    public static void assertRoute(RequestMappingHandlerMapping mappings, HttpMethod method, String path) {
        boolean found = mappings.getHandlerMethods().keySet().stream()
                .anyMatch(info -> hasMethod(info, method) && hasPath(info, path));
        assertTrue(found, () -> "未注册路由: " + method + " " + path);
    }

    public static void assertNoRoute(RequestMappingHandlerMapping mappings, HttpMethod method, String path) {
        boolean found = mappings.getHandlerMethods().keySet().stream()
                .anyMatch(info -> hasMethod(info, method) && hasPath(info, path));
        assertTrue(!found, () -> "不应注册路由: " + method + " " + path);
    }

    private static boolean hasMethod(RequestMappingInfo info, HttpMethod method) {
        return info.getMethodsCondition().getMethods().stream().anyMatch(value -> value.asHttpMethod() == method);
    }

    private static boolean hasPath(RequestMappingInfo info, String path) {
        Set<String> patterns = info.getPathPatternsCondition() == null
                ? info.getPatternsCondition().getPatterns().stream().map(Object::toString).collect(java.util.stream.Collectors.toSet())
                : info.getPathPatternsCondition().getPatternValues();
        return patterns.contains(path);
    }
}
