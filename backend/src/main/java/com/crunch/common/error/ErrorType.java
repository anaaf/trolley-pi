package com.crunch.common.error;

import lombok.Getter;

@Getter
public enum ErrorType {

    ENTITY_NOT_FOUND(10000, "Entity Not Found."),
    PRODUCT_NOT_FOUND(10001, "Product Not Found"),
    BARCODE_NOT_FOUND(10002, "Barcode Not Found"),
    ENTITY_ALREADY_EXISTS(10003, "Entity Already exists"),
    CART_NOT_FOUND(10004, "Cart Not Found"),
    REQUIRED_FIELD_MISSING(10005, "Required Field Missing.");

    private final int code;
    private final String message;

    ErrorType(int code, String message) {
        this.code = code;
        this.message = message;

    }
}
