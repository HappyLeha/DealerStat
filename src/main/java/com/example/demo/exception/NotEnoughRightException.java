package com.example.demo.exception;

public class NotEnoughRightException extends RuntimeException {
    private String message;

    public NotEnoughRightException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
