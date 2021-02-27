package me.jysh.moviereviewservice.exception;

public class MovieNotFoundException extends IllegalArgumentException {

  public MovieNotFoundException() {}

  public MovieNotFoundException(String s) {

    super(s);
  }

  public MovieNotFoundException(String message, Throwable cause) {

    super(message, cause);
  }

  public MovieNotFoundException(Throwable cause) {

    super(cause);
  }
}
