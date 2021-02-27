package me.jysh.moviereviewservice.config;

import me.jysh.moviereviewservice.repository.MovieRepository;
import me.jysh.moviereviewservice.repository.ReviewRepository;
import me.jysh.moviereviewservice.repository.UserRepository;
import me.jysh.moviereviewservice.service.MovieReviewService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {

  @Bean
  public MovieReviewService movieReviewService(
      UserRepository userRepository,
      MovieRepository movieRepository,
      ReviewRepository reviewRepository) {

    return new MovieReviewService(userRepository, reviewRepository, movieRepository);
  }
}
