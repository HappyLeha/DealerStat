package com.example.demo.exception;

public class InvalidPasswordException extends RuntimeException {

    @Override
    public String getMessage() {
        return "This password is wrong.";
    }
}
