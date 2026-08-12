package com.team.ecommerce.common;

/**
 * 统一响应包装。契约约定外层结构固定为 {@code {code, message, data}}，
 * 失败时 {@code data} 为 {@code null}。
 *
 * @param code    业务状态码（与 HTTP 状态码一致，见契约 0.4）
 * @param message 提示信息
 * @param data    业务数据，失败时为 null
 */
public record Result<T>(int code, String message, T data) {

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.code(), ResultCode.SUCCESS.defaultMessage(), data);
    }

    public static <T> Result<T> success(T data, String message) {
        return new Result<>(ResultCode.SUCCESS.code(), message, data);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(ResultCode rc) {
        return new Result<>(rc.code(), rc.defaultMessage(), null);
    }

    public static <T> Result<T> error(ResultCode rc, String message) {
        return new Result<>(rc.code(), message, null);
    }
}
