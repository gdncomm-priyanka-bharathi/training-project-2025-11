package com.app.cartservice.exceptions;

public class CartDoesNotExistsException extends RuntimeException {
    public CartDoesNotExistsException(String message) {
        super(message);
    }
}
