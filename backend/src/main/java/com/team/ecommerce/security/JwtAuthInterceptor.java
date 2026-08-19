package com.team.ecommerce.security;

import com.team.ecommerce.auth.entity.User;
import com.team.ecommerce.auth.mapper.UserMapper;
import com.team.ecommerce.common.BizException;
import com.team.ecommerce.common.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 鉴权拦截器：
 * <ul>
 *   <li>解析 {@code Authorization: Bearer &lt;token&gt;}，无效/过期抛 401；</li>
 *   <li>按 token 中用户 id 加载用户，写入 {@link UserContext}；</li>
 *   <li>预留角色路径拦截：{@code /api/merchant/**} 需 MERCHANT、{@code /api/admin/**} 需 ADMIN。</li>
 * </ul>
 * 抛出的 {@link BizException} 由 {@code GlobalExceptionHandler} 统一转成 JSON + HTTP 状态码。
 */
@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    public JwtAuthInterceptor(JwtUtil jwtUtil, UserMapper userMapper) {
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new BizException(ResultCode.UNAUTHORIZED);
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        Long userId = jwtUtil.parseUserId(token);

        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BizException(ResultCode.UNAUTHORIZED, "账号不存在");
        }

        UserContext.set(new UserContext.LoginUser(user.getId(), user.getUsername(), user.getRole()));

        String uri = request.getRequestURI();
        String role = user.getRole();
        if (uri.startsWith("/api/merchant/") && !"MERCHANT".equals(role)) {
            throw new BizException(ResultCode.FORBIDDEN);
        }
        if (uri.startsWith("/api/admin/") && !"ADMIN".equals(role)) {
            throw new BizException(ResultCode.FORBIDDEN);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
