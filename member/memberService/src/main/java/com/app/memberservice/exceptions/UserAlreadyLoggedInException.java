package com.app.memberservice.exceptions;

public class UserAlreadyLoggedInException extends RuntimeException {
  public UserAlreadyLoggedInException(String message) {
    super(message);
  }
}
