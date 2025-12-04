package com.app.memberservice.exceptions;

public class InvalidUserOrPassword extends RuntimeException {
  public InvalidUserOrPassword(String message) {
    super(message);
  }
}
