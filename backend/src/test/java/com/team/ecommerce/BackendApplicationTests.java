package com.team.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 上下文加载冒烟测试。{@link ActiveProfiles("test")} 确保即使在全量 mvn test 下
 * 也连接专用测试库 jd_ecommerce_test，绝不触碰开发库 jd_ecommerce。
 */
@SpringBootTest
@ActiveProfiles("test")
class BackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
