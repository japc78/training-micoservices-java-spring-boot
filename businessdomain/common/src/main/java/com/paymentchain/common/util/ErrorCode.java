package com.paymentchain.common.util;

public enum ErrorCode {
    UNKNOWN_HOST("G-001", "Connection Error, unknow host"),
    DATABASE_ERROR("DB-001", "Database error");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return code + ": " + message;
    }
}
