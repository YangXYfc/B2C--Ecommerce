package com.team.ecommerce.security;

/**
 * 当前登录用户上下文（ThreadLocal）。由 {@link JwtAuthInterceptor} 在请求进入时写入、
 * 请求结束时清理，Service 层通过 {@link #getUserId()} 取当前用户 id。
 */
public final class UserContext {

    /** 当前登录用户信息。 */
    public record LoginUser(Long id, String username, String role) {
    }

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(LoginUser user) {
        HOLDER.set(user);
    }

    public static LoginUser get() {
        return HOLDER.get();
    }

    /** 当前用户 id；未登录时为 null。 */
    public static Long getUserId() {
        LoginUser user = HOLDER.get();
        return user == null ? null : user.id();
    }

    /** 当前用户角色；未登录时为 null。 */
    public static String getRole() {
        LoginUser user = HOLDER.get();
        return user == null ? null : user.role();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
