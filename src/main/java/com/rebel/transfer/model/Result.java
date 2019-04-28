package com.rebel.transfer.model;

import java.util.Optional;

public class Result<T> {

    private final boolean result;
    private final String  errorMessage;
    private final T       value;

    private Result(boolean result, String errorMessage, T value) {
        this.result = result;
        this.errorMessage = errorMessage;
        this.value = value;
    }

    public boolean succeeded() {
        return result;
    }

    public boolean failed() {
        return !result;
    }

    public String errorMessage() {
        return errorMessage;
    }

    public Optional<T> value() {
        return Optional.ofNullable(value);
    }

    public static <T> Result<T> success() {
        return new Result<>(true, null, null);
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(true, null, value);
    }

    public static <T> Result<T> fail(String errorMessage) {
        return new Result<>(false, errorMessage, null);
    }
}
