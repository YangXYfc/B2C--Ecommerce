package com.team.ecommerce.common;

/**
 * 业务异常：Service 层校验/业务规则失败时抛出，由 {@link GlobalExceptionHandler}
 * 统一转成 {@code {code, message, data:null}} 响应并设置对应的 HTTP 状态码。
 */
public class BizException extends RuntimeException {

    private final int code;

    public BizException(ResultCode rc) {
        super(rc.defaultMessage());
        this.code = rc.code();
    }

    public BizException(ResultCode rc, String message) {
        super(message);
        this.code = rc.code();
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
