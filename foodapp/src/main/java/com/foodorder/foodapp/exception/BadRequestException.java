package com.foodorder.foodapp.exception;

import lombok.Getter;

/**
 * Business rule violation. The message is an i18n key; {@code args} fill its placeholders ({0}, {1}...).
 */
@Getter
public class BadRequestException extends RuntimeException {
    private final transient Object[] args;

    public BadRequestException(String message, Object... args) {
        super(message);
        this.args = args;
    }
}
