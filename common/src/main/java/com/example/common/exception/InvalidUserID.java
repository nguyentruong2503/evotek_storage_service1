package com.example.common.exception;

public class InvalidUserID extends RuntimeException {
    public InvalidUserID(String message) {
        super(message);
    }
}
