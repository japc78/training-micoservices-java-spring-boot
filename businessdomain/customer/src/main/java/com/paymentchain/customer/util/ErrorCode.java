package com.paymentchain.customer.util;

public enum ErrorCode {
    CUSTOMER_NOT_FOUND("C-001", "Customer not found"),
    PRODUCT_NOT_FOUND("P-001", "Product not found"),
    DUPLICATE_CUSTOMER("C-002", "Duplicate customer entry"),
    DUPLICATE_PRODUCT("P-002", "Duplicate product entry"),
    INVALID_PRODUCT("P-003", "Invalid product details");

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
