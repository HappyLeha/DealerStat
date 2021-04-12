package com.example.demo.exception;

public class UnknownServerException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Unknown server error";
    }
}
