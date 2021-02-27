package me.jysh.moviereviewservice.exception;

public class MovieNotYetReleasedException extends IllegalArgumentException {

  public MovieNotYetReleasedException() {

    super();
  }

  public MovieNotYetReleasedException(String s) {

    super(s);
  }

  public MovieNotYetReleasedException(String message, Throwable cause) {

    super(message, cause);
  }

  public MovieNotYetReleasedException(Throwable cause) {

    super(cause);
  }
}
