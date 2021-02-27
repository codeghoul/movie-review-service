package me.jysh.moviereviewservice.exception;

public class MultipleReviewsNotAllowedException extends IllegalArgumentException {

  public MultipleReviewsNotAllowedException() {}

  public MultipleReviewsNotAllowedException(String s) {

    super(s);
  }

  public MultipleReviewsNotAllowedException(String message, Throwable cause) {

    super(message, cause);
  }

  public MultipleReviewsNotAllowedException(Throwable cause) {

    super(cause);
  }
}
