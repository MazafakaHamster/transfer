package com.rebel.transfer.web.router.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message, Object... args) {
        super(String.format(message, args));
    }
}
