package com.team.ecommerce.config;

import com.team.ecommerce.security.JwtAuthInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/**
 * Web 层配置：跨域、JWT 鉴权拦截器、密码加密器、上传目录静态资源。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtAuthInterceptor jwtAuthInterceptor;
    private final String uploadDir;

    public WebConfig(JwtAuthInterceptor jwtAuthInterceptor,
                     @Value("${upload.dir:upload/}") String uploadDir) {
        this.jwtAuthInterceptor = jwtAuthInterceptor;
        this.uploadDir = uploadDir;
    }

    /** 密码加密器：默认强度 10，与种子数据 $2a$10$ 哈希匹配。 */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** 跨域配置：演示环境放行；开启凭据时必须使用 allowedOriginPatterns。 */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /** JWT 拦截：除注册/登录及公开接口外，/api/** 全部要求登录。 */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/register", "/api/auth/login",
                        "/api/categories", "/api/products", "/api/products/*",
                        "/api/products/*/reviews", "/api/banners", "/upload/**");
    }

    /** 上传目录静态资源映射：/upload/** → file:{upload.dir}/，访问无需 JWT。 */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Path.of(uploadDir).toAbsolutePath().normalize().toUri().toString() + "/";
        registry.addResourceHandler("/upload/**").addResourceLocations(location);
    }
}
