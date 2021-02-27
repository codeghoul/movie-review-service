package me.jysh.moviereviewservice.repository;

import me.jysh.moviereviewservice.model.Review;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewRepository extends BaseRepository<Review> {

  public boolean hasOldReview(String userId, String movieId) {

    return !findAllBy(
            review -> review.getUserId().equals(userId) && review.getMovieId().equals(movieId))
        .isEmpty();
  }
}
