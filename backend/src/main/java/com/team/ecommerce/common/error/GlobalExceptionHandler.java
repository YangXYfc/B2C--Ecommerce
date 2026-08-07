package com.team.ecommerce.common.error;

import com.team.ecommerce.common.api.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FeatureNotImplementedException.class)
    ResponseEntity<ApiResponse<Void>> handleNotImplemented(FeatureNotImplementedException exception) {
        return response(HttpStatus.NOT_IMPLEMENTED, exception.getErrorCode(), exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class,
            HttpMessageNotReadableException.class, ServletRequestBindingException.class,
            IllegalArgumentException.class})
    ResponseEntity<ApiResponse<Void>> handleInvalidArgument(Exception exception) {
        return response(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_ARGUMENT, "请求参数不正确");
    }

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException exception) {
        return response(HttpStatus.CONFLICT, exception.getErrorCode(), exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception exception) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR, "系统内部错误");
    }

    private ResponseEntity<ApiResponse<Void>> response(HttpStatus status, ErrorCode code, String message) {
        return ResponseEntity.status(status).body(ApiResponse.error(code.name(), message));
    }
}
