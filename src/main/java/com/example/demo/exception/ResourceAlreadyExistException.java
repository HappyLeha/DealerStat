package com.example.demo.exception;

public class ResourceAlreadyExistException extends RuntimeException {
    private String message;

    public ResourceAlreadyExistException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
