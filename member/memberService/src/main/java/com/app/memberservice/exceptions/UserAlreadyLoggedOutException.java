package com.app.memberservice.exceptions;

public class UserAlreadyLoggedOutException extends RuntimeException {
    public UserAlreadyLoggedOutException(String message) {
        super(message);
    }
}
