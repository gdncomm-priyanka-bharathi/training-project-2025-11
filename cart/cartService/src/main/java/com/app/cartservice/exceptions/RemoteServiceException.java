package com.app.cartservice.exceptions;

public class RemoteServiceException extends RuntimeException {
  public RemoteServiceException(String message) {
    super(message);
  }
}
