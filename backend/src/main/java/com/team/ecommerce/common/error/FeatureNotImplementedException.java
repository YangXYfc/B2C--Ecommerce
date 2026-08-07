package com.team.ecommerce.common.error;

public final class FeatureNotImplementedException extends BusinessException {

    public FeatureNotImplementedException(String operation) {
        super(ErrorCode.NOT_IMPLEMENTED, "功能待实现: " + operation);
    }
}
