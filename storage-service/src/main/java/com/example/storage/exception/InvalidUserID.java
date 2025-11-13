package com.example.storage.exception;

public class InvalidUserID extends RuntimeException {
    public InvalidUserID(String message) {
        super(message);
    }
}
