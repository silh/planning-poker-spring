package com.silh.planningpokerspring.exception;

public class GameNotFoundException extends RuntimeException {

  public GameNotFoundException(String message) {
    super(message);
  }
}
