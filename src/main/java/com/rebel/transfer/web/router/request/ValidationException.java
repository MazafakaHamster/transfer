package com.rebel.transfer.web.router.request;

public class ValidationException extends RuntimeException {
    ValidationException(String message, Object... args) {
        super(String.format(message, args));
    }
}
