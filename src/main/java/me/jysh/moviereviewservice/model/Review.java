package me.jysh.moviereviewservice.model;

import lombok.Getter;

@Getter
public class Review extends BaseEntity {

  public static final int CRITIC_RATING_WEIGHT = 2;

  private final Integer rating;

  private final Boolean isCriticReview;

  private final String userId;

  private final String movieId;

  public Review(String userId, String movieId, Integer rating, boolean isUserCritic) {

    this.userId = userId;
    this.movieId = movieId;
    this.rating = rating;
    this.isCriticReview = isUserCritic;
  }

  public int getRating() {

    return this.isCriticReview ? this.rating * CRITIC_RATING_WEIGHT : this.rating;
  }
}
